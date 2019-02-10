package au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.senate_count_data

import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.FetchSenateCountData
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.senate_count_data.FetchSenateCountDataFromRaw._
import au.id.tmm.ausvotes.data_sources.aec.federal.raw.FetchRawSenateDistributionOfPreferences
import au.id.tmm.ausvotes.data_sources.common.Fs2Interop._
import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.ausvotes.model.federal.senate.{SenateCandidate, SenateCountData, SenateElectionForState, SenateGroupsAndCandidates}
import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError.Ops
import au.id.tmm.ausvotes.shared.io.typeclasses.{SyncEffects, BifunctorMonadError => BME}
import au.id.tmm.countstv.model._
import au.id.tmm.countstv.model.countsteps._
import au.id.tmm.countstv.model.values.{Count, NumPapers, NumVotes, Ordinal}
import au.id.tmm.countstv.rules.RoundingRules
import au.id.tmm.utilities.collection.DupelessSeq
import cats.instances.int._
import fs2.Stream

final class FetchSenateCountDataFromRaw[F[+_, +_] : FetchRawSenateDistributionOfPreferences : SyncEffects] extends FetchSenateCountData[F] {

  override def senateCountDataFor(election: SenateElectionForState, groupsAndCandidates: SenateGroupsAndCandidates): F[FetchSenateCountData.Error, SenateCountData] =
    for {
      rawDopRows <- implicitly[FetchRawSenateDistributionOfPreferences[F]].senateDistributionOfPreferencesFor(election)
        .leftMap(FetchSenateCountData.Error)

      rawDopRowsGroupedByCount = rawDopRows.groupAdjacentBy(row => row.count)

      rowsForFirstCountStep <- rawDopRowsGroupedByCount.head
        .compile.lastOrError.swallowThrowablesAndWrapIn(FetchSenateCountData.Error)
        .map { case (_, firstCountStepRowsChunk) => firstCountStepRowsChunk.toVector }

      groupedRowsForOtherCountSteps = rawDopRowsGroupedByCount.tail
        .map { case (_, otherCountStepRowsChunk) => otherCountStepRowsChunk.toVector }

      countData <- countDataFrom(election, groupsAndCandidates, rowsForFirstCountStep, groupedRowsForOtherCountSteps)
        .leftMap(FetchSenateCountData.Error)

    } yield countData

  private def countDataFrom(
                             election: SenateElectionForState,
                             groupsAndCandidates: SenateGroupsAndCandidates,

                             rowsForFirstCountStep: Vector[FetchRawSenateDistributionOfPreferences.Row],
                             groupedRowsForOtherCountSteps: Stream[F[Throwable, +?], Vector[FetchRawSenateDistributionOfPreferences.Row]],
                           ): F[CountError, SenateCountData] = {

    for {
      initialMetadataAndCounts <- BME.fromEither(
        produceInitialPosition(
          election,
          groupsAndCandidates,
          rowsForFirstCountStep,
        )
      )

      finalMetadataAndCountsStream = groupedRowsForOtherCountSteps.fold(initialMetadataAndCounts) {
        case (metaDataAndCountsSoFar: MetadataAndCountsSoFar, rowsForThisCountStep: Vector[FetchRawSenateDistributionOfPreferences.Row]) =>
          appendDistributionStep(rowsForThisCountStep, metaDataAndCountsSoFar) match {
            case Right(value) => value
            case Left(countError) => throw countError // Will re-wrap this below
          }
      }

      finalMetadataAndCounts <- finalMetadataAndCountsStream.compile.lastOrError
        .swallowThrowablesAndWrapIn(CountError.wrap)

    } yield SenateCountData(
      election,
      CompletedCount[SenateCandidate](
        CountParams[SenateCandidate](
          groupsAndCandidates.candidates,
          finalMetadataAndCounts.countSteps.initialAllocation.candidateStatuses.ineligibleCandidates,
          finalMetadataAndCounts.numVacancies,
          RoundingRules.AEC,
        ),
        finalMetadataAndCounts.totalFormalPapers,
        finalMetadataAndCounts.quota,
        finalMetadataAndCounts.countSteps,
      )
    )

  }

}

object FetchSenateCountDataFromRaw {

  def apply[F[+_, +_] : FetchRawSenateDistributionOfPreferences : SyncEffects]: FetchSenateCountDataFromRaw[F] = new FetchSenateCountDataFromRaw

  sealed abstract class CountError extends ExceptionCaseClass

  object CountError {

    def wrap(exception: Exception): CountError = exception match {
      case c: CountError => c
      case e: Exception => UnexpectedException(e)
    }

    final case class InvalidNumRowsInCount(count: Count) extends CountError
    final case class DistributionSourceError(cause: Exception) extends CountError with ExceptionCaseClass.WithCause
    final case class NoDistributionSource(count: Count) extends CountError
    final case class UnexpectedException(cause: Exception) extends CountError

  }

  private final case class MetadataAndCountsSoFar(
                                                   numVacancies: Int,
                                                   totalFormalPapers: NumPapers,
                                                   quota: NumVotes,

                                                   candidatePositionLookup: Map[Int, SenateCandidate],
                                                   distributionSourceCalculator: DistributionSourceCalculator,

                                                   countSteps: CountSteps.AllowingAppending[SenateCandidate],

                                                   nextDistributionSource: Option[DistributionCountStep.Source[SenateCandidate]],
                                                 )

  private final case class InitialPositionAndMetadata(
                                                       initialAllocation: InitialAllocation[SenateCandidate],
                                                       allocationAfterIneligibles: AllocationAfterIneligibles[SenateCandidate],
                                                       nextDistributionSource: Option[DistributionCountStep.Source[SenateCandidate]],
                                                       numVacancies: Int,
                                                       totalFormalPapers: NumPapers,
                                                       quota: NumVotes,
                                                     )

  private def produceInitialPosition(
                                      election: SenateElectionForState,
                                      allGroupsAndCandidates: SenateGroupsAndCandidates,
                                      rawRowsForFirstCountStep: Vector[FetchRawSenateDistributionOfPreferences.Row],
                                    ): Either[CountError, MetadataAndCountsSoFar] = {

    val groupsAndCandidates = allGroupsAndCandidates.findFor(election)
    val candidatePositionLookup = constructBallotPositionLookup(groupsAndCandidates)
    val distributionSourceCalculator = new DistributionSourceCalculator(groupsAndCandidates.candidates)

    readInitialAllocation(rawRowsForFirstCountStep, candidatePositionLookup, distributionSourceCalculator).map { initialPositionAndMetadata =>
      MetadataAndCountsSoFar(
        numVacancies = initialPositionAndMetadata.numVacancies,
        totalFormalPapers = initialPositionAndMetadata.totalFormalPapers,
        quota = initialPositionAndMetadata.quota,

        candidatePositionLookup = candidatePositionLookup,
        distributionSourceCalculator = distributionSourceCalculator,

        countSteps = CountSteps.AfterIneligibleHandling[SenateCandidate](
          initialPositionAndMetadata.initialAllocation,
          initialPositionAndMetadata.allocationAfterIneligibles,
        ),
        initialPositionAndMetadata.nextDistributionSource,
      )
    }
  }

  private[senate_count_data] def constructBallotPositionLookup(groupsAndCandidates: SenateGroupsAndCandidates): Map[Int, SenateCandidate] = {
    val numGroups = groupsAndCandidates.groups.size
    val candidatesInBallotOrder = groupsAndCandidates.candidates.toStream
      .sorted

    candidatesInBallotOrder.zipWithIndex
      .map {
        case (candidate, index) => (candidate, index + numGroups + 1)
      }
      .map {
        case (candidate, positionOnBallotOrdinal) => positionOnBallotOrdinal -> candidate
      }
      .toMap
  }

  private def readInitialAllocation(
                                     rows: Vector[FetchRawSenateDistributionOfPreferences.Row],
                                     candidatePositionLookup: Map[Int, SenateCandidate],
                                     distributionSourceCalculator: DistributionSourceCalculator,
                                   ): Either[CountError, InitialPositionAndMetadata] =
    for {
      parsedCountStepData <- parseNextCountStep(
        rows = rows,
        candidatePositionLookup = candidatePositionLookup,
        distributionSourceCalculator = distributionSourceCalculator,
        previouslyExcludedCandidates = DupelessSeq(),
        expectedCount = Count.ofIneligibleCandidateHandling
      )

      ineligibleCandidates = candidatePositionLookup.values.toSet diff parsedCountStepData.candidateTransfers.keySet
      initialCandidateStatuses = {
        val builder = Map.newBuilder[SenateCandidate, CandidateStatus]

        candidatePositionLookup.values.foreach { candidate =>
          builder += candidate -> CandidateStatus.Remaining
        }

        ineligibleCandidates.foreach { candidate =>
          builder += candidate -> CandidateStatus.Ineligible
        }

        parsedCountStepData.allElectedCandidates.zipWithIndex.foreach { case (candidate, orderElected) =>
          builder += candidate -> CandidateStatus.Elected(Ordinal(orderElected), Count.ofIneligibleCandidateHandling)
        }

        CandidateStatuses(builder.result())
      }

      // AEC data doesn't include transfers due to ineligibles, so we just set them to zero
      allocationAfterIneligibles = AllocationAfterIneligibles[SenateCandidate](
        candidateStatuses = initialCandidateStatuses,
        candidateVoteCounts = parsedCountStepData.candidateVoteCountsGivenNoPrevious,
        transfersDueToIneligibles = Map.empty,
      )

      // The "InitialAllocation" in our sense doesn't have any elected candidates, but otherwise it'll be the same as the
      // allocation after ineligibles.
      initialAllocation = InitialAllocation[SenateCandidate](
        candidateStatuses = CandidateStatuses(
          asMap = allocationAfterIneligibles.candidateStatuses.asMap.mapValues {
            case CandidateStatus.Elected(_, _) => CandidateStatus.Remaining
            case x => x
          }
        ),
        candidateVoteCounts = allocationAfterIneligibles.candidateVoteCounts,
      )

      countStepsSoFar = CountSteps.AfterIneligibleHandling(initialAllocation, allocationAfterIneligibles)

      nextStepDistributionSource <- distributionSourceCalculator.calculateFor(
        parsedCountStepData.distributionComment,
        countStepsSoFar,
      ).left.map(CountError.DistributionSourceError)

    } yield InitialPositionAndMetadata(
      initialAllocation,
      allocationAfterIneligibles,
      nextStepDistributionSource,
      parsedCountStepData.numVacancies,
      parsedCountStepData.totalFormalPapers,
      parsedCountStepData.quota,
    )

  private def parseNextCountStep(
                                  rows: Vector[FetchRawSenateDistributionOfPreferences.Row],
                                  candidatePositionLookup: Map[Int, SenateCandidate],
                                  distributionSourceCalculator: DistributionSourceCalculator,
                                  previouslyExcludedCandidates: DupelessSeq[SenateCandidate],
                                  expectedCount: Count,
                                ): Either[CountError, ParsedCountStepData] =
    rows match {
      case candidateTransferRows :+ exhaustedBallotsRow :+ gainLossRow => {
        val numVacancies = candidateTransferRows.head.numberOfVacancies
        val totalFormalPapers = NumPapers(candidateTransferRows.head.totalFormalPapers)
        val quota = NumVotes(candidateTransferRows.head.quota)
        val distributionComment = candidateTransferRows.last.comment

        val candidateTransfers = candidateTransferRows
          .map(row => candidatePositionLookup(row.ballotPosition) -> transfersFrom(row))
          .toMap

        val allElectedCandidates = allElectedCandidatesOf(candidatePositionLookup, candidateTransferRows)

        val allExcludedCandidates = allExcludedCandidatesOf(candidatePositionLookup, previouslyExcludedCandidates,
          candidateTransferRows)

        val exhaustedTransfers = transfersFrom(exhaustedBallotsRow)

        val gainLossTransfers = transfersFrom(gainLossRow)

        val candidatesElectedThisStep = newlyElectedCandidatesIn(candidatePositionLookup, candidateTransferRows)

        val candidateExcludedThisStep = newlyExcludedCandidateIn(candidatePositionLookup, candidateTransferRows)

        Right(
          ParsedCountStepData(
            numVacancies,
            totalFormalPapers,
            quota,
            distributionComment,
            candidateTransfers,
            allElectedCandidates,
            allExcludedCandidates,
            exhaustedTransfers,
            gainLossTransfers,
            candidatesElectedThisStep,
            candidateExcludedThisStep,
          )
        )
      }
      case _ => Left(CountError.InvalidNumRowsInCount(expectedCount))
    }

  private def appendDistributionStep(
                                      rawRowsForStep: Vector[FetchRawSenateDistributionOfPreferences.Row],
                                      metadataAndCountsSoFar: MetadataAndCountsSoFar,
                                    ): Either[CountError, MetadataAndCountsSoFar] = {
    val previousStep = metadataAndCountsSoFar.countSteps.last
    val count = previousStep.count.increment

    for {
      distributionSource <- metadataAndCountsSoFar.nextDistributionSource
        .toRight(CountError.NoDistributionSource(count))

      parsedCountStepData <- parseNextCountStep(
        rawRowsForStep,
        metadataAndCountsSoFar.candidatePositionLookup,
        metadataAndCountsSoFar.distributionSourceCalculator,
        previousStep.candidateStatuses.excludedCandidates,
        expectedCount = count,
      )

      newCandidateStatuses = accumulateCandidateOutcomes(count, previousStep.candidateStatuses, parsedCountStepData)

      thisDistributionStep = DistributionCountStep[SenateCandidate](
        count,
        newCandidateStatuses,
        parsedCountStepData.candidateVoteCountsGiven(previousStep.candidateVoteCounts),
        distributionSource,
      )

      allCountSteps = metadataAndCountsSoFar.countSteps.append(thisDistributionStep)

      nextStepDistributionSource <- metadataAndCountsSoFar.distributionSourceCalculator.calculateFor(
        parsedCountStepData.distributionComment,
        allCountSteps,
      ).left.map(CountError.DistributionSourceError)

    } yield metadataAndCountsSoFar.copy(
      countSteps = allCountSteps,
      nextDistributionSource = nextStepDistributionSource,
    )
  }

  private val excludedStatus = "Excluded"

  private def transfersFrom(row: FetchRawSenateDistributionOfPreferences.Row): ParsedCountStepTransfer = {
    ParsedCountStepTransfer(
      NumPapers(row.papers),
      NumVotes(row.votesTransferred),
      NumVotes(row.progressiveVoteTotal)
    )
  }

  private def allElectedCandidatesOf(candidatePositionLookup: Map[Int, SenateCandidate],
                                     candidateTransferRows: Vector[FetchRawSenateDistributionOfPreferences.Row]
                                    ): DupelessSeq[SenateCandidate] = {
    val candidatesInOrderElected = candidateTransferRows.toStream
      .filter(_.orderElected != 0)
      .sortBy(_.orderElected)
      .map(row => candidatePositionLookup(row.ballotPosition))

    DupelessSeq(candidatesInOrderElected :_*)
  }

  private def allExcludedCandidatesOf(candidatePositionLookup: Map[Int, SenateCandidate],
                                      previouslyExcluded: DupelessSeq[SenateCandidate],
                                      candidateTransferRows: Vector[FetchRawSenateDistributionOfPreferences.Row]
                                     ): DupelessSeq[SenateCandidate] = {
    val allExcludedCandidatesAtThisStep = candidateTransferRows.toStream
      .filter(_.status == excludedStatus)
      .map(row => candidatePositionLookup(row.ballotPosition))

    previouslyExcluded ++ allExcludedCandidatesAtThisStep
  }

  private def newlyElectedCandidatesIn(candidatePositionLookup: Map[Int, SenateCandidate],
                                       candidateTransferRows: Vector[FetchRawSenateDistributionOfPreferences.Row]
                                      ): DupelessSeq[SenateCandidate] = {
    val newlyElectedCandidates = candidateTransferRows.toStream
      .filter(_.changed contains true)
      .filter(_.orderElected != 0)
      .sortBy(_.orderElected)
      .map(row => candidatePositionLookup(row.ballotPosition))

    DupelessSeq(newlyElectedCandidates :_*)
  }

  private def newlyExcludedCandidateIn(candidatePositionLookup: Map[Int, SenateCandidate],
                                       candidateTransferRows: Vector[FetchRawSenateDistributionOfPreferences.Row]
                                      ): Option[SenateCandidate] = {
    val newlyExcludedCandidates = candidateTransferRows.toStream
      .filter(_.changed contains true)
      .filter(_.status == excludedStatus)
      .map(row => candidatePositionLookup(row.ballotPosition))
      .toSet

    if (newlyExcludedCandidates.size > 1) {
      throw new IllegalStateException("More than one candidate excluded in a single step")
    } else {
      newlyExcludedCandidates.headOption
    }
  }

  private def accumulateCandidateOutcomes(count: Count,
                                          outcomesToNow: CandidateStatuses[SenateCandidate],
                                          parsedCountStepData: ParsedCountStepData
                                         ): CandidateStatuses[SenateCandidate] = {
    val electedCandidateStatuses = parsedCountStepData.candidatesElectedThisStep
      .zipWithIndex
      .toMap
      .map { case (candidate, ordinalElectedThisStep) =>
        candidate -> CandidateStatus.Elected(
          Ordinal(ordinalElectedThisStep + outcomesToNow.electedCandidates.size),
          count,
        )
      }

    val changedCandidateStatuses = electedCandidateStatuses ++ parsedCountStepData.candidateExcludedThisStep
      .map { candidate =>
        candidate -> CandidateStatus.Excluded(Ordinal(outcomesToNow.excludedCandidates.size), count)
      }

    outcomesToNow.updateFrom(changedCandidateStatuses)
  }

  private final case class ParsedCountStepData(
                                                numVacancies: Int,
                                                totalFormalPapers: NumPapers,
                                                quota: NumVotes,
                                                distributionComment: String,
                                                candidateTransfers: Map[SenateCandidate, ParsedCountStepTransfer],
                                                allElectedCandidates: DupelessSeq[SenateCandidate],
                                                allExcludedCandidates: DupelessSeq[SenateCandidate],
                                                exhaustedTransfers: ParsedCountStepTransfer,
                                                gainLossTransfers: ParsedCountStepTransfer,
                                                candidatesElectedThisStep: DupelessSeq[SenateCandidate],
                                                candidateExcludedThisStep: Option[SenateCandidate],
                                              ) {
    def candidateVoteCountsGiven(
                                  previousCandidateVoteCounts: CandidateVoteCounts[SenateCandidate]
                                ): CandidateVoteCounts[SenateCandidate] =
      CandidateVoteCounts(
        perCandidate = candidateTransfers.map { case (candidate, transfer) =>
          candidate -> transfer.asVoteCountGiven(previousCandidateVoteCounts.perCandidate(candidate))
        },
        exhausted = exhaustedTransfers.asVoteCountGiven(previousCandidateVoteCounts.exhausted),
        roundingError = gainLossTransfers.asVoteCountGiven(previousCandidateVoteCounts.roundingError),
      )

    def candidateVoteCountsGivenNoPrevious: CandidateVoteCounts[SenateCandidate] = {
      CandidateVoteCounts(
        perCandidate = candidateTransfers.mapValues { transfer =>
          transfer.asVoteCountGiven(previousVoteCount = VoteCount.zero)
        },
        exhausted = exhaustedTransfers.asVoteCountGiven(VoteCount.zero),
        roundingError = gainLossTransfers.asVoteCountGiven(VoteCount.zero),
      )
    }
  }

  private final case class ParsedCountStepTransfer(papersTransferred: NumPapers,
                                                   votesTransferred: NumVotes,
                                                   votesTotal: NumVotes) {
    def asVoteCountGiven(previousVoteCount: VoteCount): VoteCount = {
      val newPapers = previousVoteCount.numPapers + papersTransferred
      val newVotes = previousVoteCount.numVotes + votesTransferred

      assert(newVotes == votesTotal, s"Expected $votesTotal votes, got $newVotes")

      VoteCount(newPapers, newVotes)
    }
  }

}
