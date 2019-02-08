package au.id.tmm.ausvotes.core.parsing.countdata

import au.id.tmm.ausvotes.core.model.GroupsAndCandidates
import au.id.tmm.ausvotes.core.rawdata.model.DistributionOfPreferencesRow
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.senate_count_data.DistributionSourceCalculator
import au.id.tmm.ausvotes.model.federal.senate.{SenateCandidate, SenateCountData, SenateElectionForState}
import au.id.tmm.countstv.model._
import au.id.tmm.countstv.model.countsteps.{AllocationAfterIneligibles, CountSteps, DistributionCountStep, InitialAllocation}
import au.id.tmm.countstv.model.values.{Count, NumPapers, NumVotes, Ordinal}
import au.id.tmm.countstv.rules.RoundingRules
import au.id.tmm.utilities.collection.DupelessSeq
import au.id.tmm.utilities.collection.IteratorUtils.ImprovedIterator

import scala.annotation.tailrec

object CountDataGeneration {

  private val ballotPositionForExhausted = 1001
  private val ballotPositionForGainLoss = 1002
  private val excludedStatus = "Excluded"

  private val specialBallotPositions = Set(ballotPositionForExhausted, ballotPositionForGainLoss)

  def fromDistributionOfPreferencesRows(
                                         election: SenateElectionForState,
                                         allGroupsAndCandidates: GroupsAndCandidates,
                                         distributionOfPreferencesRows: Iterator[DistributionOfPreferencesRow],
                                       ): SenateCountData = {
    val groupsAndCandidates = allGroupsAndCandidates.findFor(election)
    val candidatePositionLookup = constructBallotPositionLookup(groupsAndCandidates)
    val distributionSourceCalculator = new DistributionSourceCalculator(groupsAndCandidates.candidates)

    val initialAllocationAndMetadata = readInitialAllocation(
      distributionOfPreferencesRows, candidatePositionLookup, distributionSourceCalculator
    )

    val countSteps = readDistributionSteps(
      stepsSoFar = CountSteps.AfterIneligibleHandling(
        initialAllocationAndMetadata.initialAllocation,
        initialAllocationAndMetadata.allocationAfterIneligibles,
      ),
      initialAllocationAndMetadata.nextDistributionSource,
      candidatePositionLookup,
      distributionSourceCalculator,
      distributionOfPreferencesRows
    )

    SenateCountData(
      election,
      CompletedCount[SenateCandidate](
        CountParams[SenateCandidate](
          groupsAndCandidates.candidates,
          initialAllocationAndMetadata.initialAllocation.candidateStatuses.ineligibleCandidates,
          initialAllocationAndMetadata.numVacancies,
          RoundingRules.AEC,
        ),
        initialAllocationAndMetadata.totalFormalPapers,
        initialAllocationAndMetadata.quota,
        countSteps,
      ),
    )
  }

  private[countdata] def constructBallotPositionLookup(groupsAndCandidates: GroupsAndCandidates): Map[Int, SenateCandidate] = {
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

  private def readInitialAllocation(rows: Iterator[DistributionOfPreferencesRow],
                                    candidatePositionLookup: Map[Int, SenateCandidate],
                                    distributionSourceCalculator: DistributionSourceCalculator
                                   ): InitialPositionAndMetadata = {

    val parsedCountStepData = parseNextCountStep(
      rows = rows,
      candidatePositionLookup = candidatePositionLookup,
      distributionSourceCalculator = distributionSourceCalculator,
      previouslyExcludedCandidates = DupelessSeq(),
      expectedCount = Count.ofIneligibleCandidateHandling
    ).getOrElse(throw new IllegalStateException("No Rows in count data"))

    val ineligibleCandidates = candidatePositionLookup.values.toSet diff parsedCountStepData.candidateTransfers.keySet
    val initialCandidateStatuses = {
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
    val allocationAfterIneligibles = AllocationAfterIneligibles[SenateCandidate](
      candidateStatuses = initialCandidateStatuses,
      candidateVoteCounts = parsedCountStepData.candidateVoteCountsGivenNoPrevious,
      transfersDueToIneligibles = Map.empty,
    )

    // The "InitialAllocation" in our sense doesn't have any elected candidates, but otherwise it'll be the same as the
    // allocation after ineligibles.
    val initialAllocation = InitialAllocation[SenateCandidate](
      candidateStatuses = CandidateStatuses(
        asMap = allocationAfterIneligibles.candidateStatuses.asMap.mapValues {
          case CandidateStatus.Elected(_, _) => CandidateStatus.Remaining
          case x => x
        }
      ),
      candidateVoteCounts = allocationAfterIneligibles.candidateVoteCounts,
    )

    val countStepsSoFar = CountSteps.AfterIneligibleHandling(initialAllocation, allocationAfterIneligibles)

    val nextStepDistributionSource = distributionSourceCalculator.calculateFor(
      parsedCountStepData.distributionComment,
      countStepsSoFar,
    )

    InitialPositionAndMetadata(
      initialAllocation,
      allocationAfterIneligibles,
      nextStepDistributionSource match {
        case Right(source) => source
        case Left(exception) => throw exception
      },
      parsedCountStepData.numVacancies,
      parsedCountStepData.totalFormalPapers,
      parsedCountStepData.quota,
    )
  }

  @tailrec
  private def readDistributionSteps(stepsSoFar: CountSteps.AllowingAppending[SenateCandidate],
                                    maybeDistributionSourceForFirstStep: Option[DistributionCountStep.Source[SenateCandidate]],
                                    candidatePositionLookup: Map[Int, SenateCandidate],
                                    distributionSourceCalculator: DistributionSourceCalculator,
                                    dopRows: Iterator[DistributionOfPreferencesRow]
                                   ): CountSteps.AllowingAppending[SenateCandidate] = {
    if (maybeDistributionSourceForFirstStep.isEmpty) {
      return stepsSoFar
    }

    val distributionSourceForFirstStep = maybeDistributionSourceForFirstStep.get

    val previousStep = stepsSoFar.last

    val count = previousStep.count.increment

    parseNextCountStep(
      dopRows,
      candidatePositionLookup,
      distributionSourceCalculator,
      previouslyExcludedCandidates = previousStep.candidateStatuses.excludedCandidates,
      count
    ) match {
      case None => stepsSoFar
      case Some(parsedCountStepData) => {
        val newCandidateStatuses = accumulateCandidateOutcomes(count, previousStep.candidateStatuses, parsedCountStepData)

        val thisDistributionStep = DistributionCountStep[SenateCandidate](
          count,
          newCandidateStatuses,
          parsedCountStepData.candidateVoteCountsGiven(previousStep.candidateVoteCounts),
          distributionSourceForFirstStep,
        )

        val allCountSteps = stepsSoFar.append(thisDistributionStep)

        val nextStepDistributionSource = distributionSourceCalculator.calculateFor(
          parsedCountStepData.distributionComment,
          allCountSteps,
        )

        readDistributionSteps(
          allCountSteps,
          nextStepDistributionSource match {
            case Right(source) => source
            case Left(exception) => throw exception
          },
          candidatePositionLookup,
          distributionSourceCalculator,
          dopRows
        )
      }
    }
  }

  private def parseNextCountStep(rows: Iterator[DistributionOfPreferencesRow],
                                 candidatePositionLookup: Map[Int, SenateCandidate],
                                 distributionSourceCalculator: DistributionSourceCalculator,
                                 previouslyExcludedCandidates: DupelessSeq[SenateCandidate],
                                 expectedCount: Count
                                ): Option[ParsedCountStepData] = {
    if (!rows.hasNext) {
      return None
    }

    val (candidateTransferRows, exhaustedBallotsRow, gainLossRow) =
      readRowsForNextCount(rows, candidatePositionLookup, expectedCount)

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

    Some(
      ParsedCountStepData(numVacancies, totalFormalPapers, quota, distributionComment, candidateTransfers,
        allElectedCandidates, allExcludedCandidates, exhaustedTransfers, gainLossTransfers, candidatesElectedThisStep,
        candidateExcludedThisStep)
    )
  }

  private def readRowsForNextCount(rows: Iterator[DistributionOfPreferencesRow],
                                   candidatePositionLookup: Map[Int, SenateCandidate],
                                   expectedCount: Count
                                  ): (Vector[DistributionOfPreferencesRow], DistributionOfPreferencesRow, DistributionOfPreferencesRow) = {
    val countRows = rows.readUntil(row => row.ballotPosition == ballotPositionForGainLoss)
    assert(countRows.forall(_.count == expectedCount.asInt))

    val candidateTransferRows = countRows.filterNot(row => specialBallotPositions.contains(row.ballotPosition))

    val exhaustedBallotsRow = countRows(candidateTransferRows.size)
    assert(exhaustedBallotsRow.ballotPosition == ballotPositionForExhausted)

    val gainLossRow = countRows(candidateTransferRows.size + 1)
    assert(gainLossRow.ballotPosition == ballotPositionForGainLoss)

    (candidateTransferRows, exhaustedBallotsRow, gainLossRow)
  }

  private def transfersFrom(row: DistributionOfPreferencesRow): ParsedCountStepTransfer = {
    ParsedCountStepTransfer(
      NumPapers(row.papers),
      NumVotes(row.votesTransferred),
      NumVotes(row.progressiveVoteTotal)
    )
  }

  private def allElectedCandidatesOf(candidatePositionLookup: Map[Int, SenateCandidate],
                                     candidateTransferRows: Vector[DistributionOfPreferencesRow]
                                    ): DupelessSeq[SenateCandidate] = {
    val candidatesInOrderElected = candidateTransferRows.toStream
      .filter(_.orderElected != 0)
      .sortBy(_.orderElected)
      .map(row => candidatePositionLookup(row.ballotPosition))

    DupelessSeq(candidatesInOrderElected :_*)
  }

  private def allExcludedCandidatesOf(candidatePositionLookup: Map[Int, SenateCandidate],
                                      previouslyExcluded: DupelessSeq[SenateCandidate],
                                      candidateTransferRows: Vector[DistributionOfPreferencesRow]
                                     ): DupelessSeq[SenateCandidate] = {
    val allExcludedCandidatesAtThisStep = candidateTransferRows.toStream
      .filter(_.status == excludedStatus)
      .map(row => candidatePositionLookup(row.ballotPosition))

    previouslyExcluded ++ allExcludedCandidatesAtThisStep
  }

  private def newlyElectedCandidatesIn(candidatePositionLookup: Map[Int, SenateCandidate],
                                       candidateTransferRows: Vector[DistributionOfPreferencesRow]
                                      ): DupelessSeq[SenateCandidate] = {
    val newlyElectedCandidates = candidateTransferRows.toStream
      .filter(_.changed contains true)
      .filter(_.orderElected != 0)
      .sortBy(_.orderElected)
      .map(row => candidatePositionLookup(row.ballotPosition))

    DupelessSeq(newlyElectedCandidates :_*)
  }

  private def newlyExcludedCandidateIn(candidatePositionLookup: Map[Int, SenateCandidate],
                                       candidateTransferRows: Vector[DistributionOfPreferencesRow]
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

  private final case class InitialPositionAndMetadata(initialAllocation: InitialAllocation[SenateCandidate],
                                                      allocationAfterIneligibles: AllocationAfterIneligibles[SenateCandidate],
                                                      nextDistributionSource: Option[DistributionCountStep.Source[SenateCandidate]],
                                                      numVacancies: Int,
                                                      totalFormalPapers: NumPapers,
                                                      quota: NumVotes)

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
