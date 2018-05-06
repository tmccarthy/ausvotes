package au.id.tmm.ausvotes.core.parsing.countdata

import au.id.tmm.ausvotes.core.model.parsing.CandidatePosition
import au.id.tmm.ausvotes.core.model.parsing.CandidatePosition.constructBallotPositionLookup
import au.id.tmm.ausvotes.core.model.{CountData, GroupsAndCandidates, SenateElection}
import au.id.tmm.ausvotes.core.rawdata.model.DistributionOfPreferencesRow
import au.id.tmm.countstv.model.countsteps.{AllocationAfterIneligibles, CountSteps, DistributionCountStep, InitialAllocation}
import au.id.tmm.countstv.model.values.{Count, NumPapers, NumVotes, Ordinal}
import au.id.tmm.countstv.model.{CandidateStatus, CandidateStatuses, CandidateVoteCounts, VoteCount}
import au.id.tmm.utilities.collection.DupelessSeq
import au.id.tmm.utilities.collection.IteratorUtils.ImprovedIterator
import au.id.tmm.utilities.geo.australia.State

import scala.annotation.tailrec

object CountDataGeneration {

  private val ballotPositionForExhausted = 1001
  private val ballotPositionForGainLoss = 1002
  private val excludedStatus = "Excluded"

  private val specialBallotPositions = Set(ballotPositionForExhausted, ballotPositionForGainLoss)

  def fromDistributionOfPreferencesRows(election: SenateElection,
                                        state: State,
                                        allGroupsAndCandidates: GroupsAndCandidates,
                                        distributionOfPreferencesRows: Iterator[DistributionOfPreferencesRow]
                                       ): CountData = {
    val groupsAndCandidates = allGroupsAndCandidates.findFor(election, state)
    val candidatePositionLookup = constructBallotPositionLookup(groupsAndCandidates)
    val distributionSourceCalculator = new DistributionSourceCalculator(groupsAndCandidates.candidates)

    val initialAllocationAndMetadata = readInitialAllocation(
      distributionOfPreferencesRows, candidatePositionLookup, distributionSourceCalculator
    )

    val countSteps = readDistributionSteps(
      stepsSoFar = CountSteps(
        initialAllocationAndMetadata.initialAllocation,
        Some(initialAllocationAndMetadata.allocationAfterIneligibles),
        distributionCountSteps = Nil
      ),
      initialAllocationAndMetadata.nextDistributionSource,
      candidatePositionLookup,
      distributionSourceCalculator,
      distributionOfPreferencesRows
    )

    CountData(
      election,
      state,
      initialAllocationAndMetadata.totalFormalPapers,
      initialAllocationAndMetadata.quota,
      countSteps,
    )
  }

  private def readInitialAllocation(rows: Iterator[DistributionOfPreferencesRow],
                                    candidatePositionLookup: Map[Int, CandidatePosition],
                                    distributionSourceCalculator: DistributionSourceCalculator
                                   ): InitialPositionAndMetadata = {

    val parsedCountStepData = parseNextCountStep(
      rows = rows,
      candidatePositionLookup = candidatePositionLookup,
      distributionSourceCalculator = distributionSourceCalculator,
      previouslyExcludedCandidates = DupelessSeq(),
      expectedCount = Count.ofIneligibleCandidateHandling
    )

    val ineligibleCandidates = candidatePositionLookup.values.toSet diff parsedCountStepData.candidateTransfers.keySet
    val initialCandidateStatuses = {
      val builder = Map.newBuilder[CandidatePosition, CandidateStatus]

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
    val allocationAfterIneligibles = AllocationAfterIneligibles[CandidatePosition](
      candidateStatuses = initialCandidateStatuses,
      candidateVoteCounts = parsedCountStepData.candidateVoteCountsGivenNoPrevious,
      transfersDueToIneligibles = Map.empty,
    )

    // The "InitialAllocation" in our sense doesn't have any elected candidates, but otherwise it'll be the same as the
    // allocation after ineligibles.
    val initialAllocation = InitialAllocation[CandidatePosition](
      candidateStatuses = CandidateStatuses(
        asMap = allocationAfterIneligibles.candidateStatuses.asMap.mapValues {
          case CandidateStatus.Elected(_, _) => CandidateStatus.Remaining
          case x => x
        }
      ),
      candidateVoteCounts = allocationAfterIneligibles.candidateVoteCounts,
    )

    val countStepsSoFar = CountSteps(initialAllocation, Some(allocationAfterIneligibles), distributionCountSteps = Nil)

    val nextStepDistributionSource = distributionSourceCalculator.calculateFor(
      parsedCountStepData.distributionComment,
      countStepsSoFar,
    )

    InitialPositionAndMetadata(
      initialAllocation,
      allocationAfterIneligibles,
      nextStepDistributionSource,
      parsedCountStepData.totalFormalPapers,
      parsedCountStepData.quota,
    )
  }

  @tailrec
  private def readDistributionSteps(stepsSoFar: CountSteps[CandidatePosition],
                                    distributionSourceForFirstStep: Option[DistributionCountStep.Source[CandidatePosition]],
                                    candidatePositionLookup: Map[Int, CandidatePosition],
                                    distributionSourceCalculator: DistributionSourceCalculator,
                                    dopRows: Iterator[DistributionOfPreferencesRow]
                                   ): CountSteps[CandidatePosition] = {
    if (distributionSourceForFirstStep.isEmpty) {
      return stepsSoFar
    }

    val previousStep = stepsSoFar.last

    val count = previousStep.count.increment

    val parsedCountStepData = parseNextCountStep(
      dopRows,
      candidatePositionLookup,
      distributionSourceCalculator,
      previouslyExcludedCandidates = previousStep.candidateStatuses.excludedCandidates,
      count
    )

    val newCandidateStatuses = accumulateCandidateOutcomes(count, previousStep.candidateStatuses, parsedCountStepData)

    val thisDistributionStep = DistributionCountStep[CandidatePosition](
      count,
      newCandidateStatuses,
      parsedCountStepData.candidateVoteCountsGiven(previousStep.candidateVoteCounts),
      distributionSourceForFirstStep
    )

    val allCountSteps = stepsSoFar.append(thisDistributionStep)

    val nextStepDistributionSource = distributionSourceCalculator.calculateFor(
      parsedCountStepData.distributionComment,
      allCountSteps,
    )

    readDistributionSteps(
      allCountSteps,
      nextStepDistributionSource,
      candidatePositionLookup,
      distributionSourceCalculator,
      dopRows
    )
  }

  private def parseNextCountStep(rows: Iterator[DistributionOfPreferencesRow],
                                 candidatePositionLookup: Map[Int, CandidatePosition],
                                 distributionSourceCalculator: DistributionSourceCalculator,
                                 previouslyExcludedCandidates: DupelessSeq[CandidatePosition],
                                 expectedCount: Count
                                ): ParsedCountStepData = {
    val (candidateTransferRows, exhaustedBallotsRow, gainLossRow) =
      readRowsForNextCount(rows, candidatePositionLookup, expectedCount)

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

    ParsedCountStepData(totalFormalPapers, quota, distributionComment, candidateTransfers, allElectedCandidates,
      allExcludedCandidates, exhaustedTransfers, gainLossTransfers, candidatesElectedThisStep,
      candidateExcludedThisStep)
  }

  private def readRowsForNextCount(rows: Iterator[DistributionOfPreferencesRow],
                                   candidatePositionLookup: Map[Int, CandidatePosition],
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

  private def allElectedCandidatesOf(candidatePositionLookup: Map[Int, CandidatePosition],
                                     candidateTransferRows: Vector[DistributionOfPreferencesRow]
                                    ): DupelessSeq[CandidatePosition] = {
    val candidatesInOrderElected = candidateTransferRows.toStream
      .filter(_.orderElected != 0)
      .sortBy(_.orderElected)
      .map(row => candidatePositionLookup(row.ballotPosition))

    DupelessSeq(candidatesInOrderElected :_*)
  }

  private def allExcludedCandidatesOf(candidatePositionLookup: Map[Int, CandidatePosition],
                                      previouslyExcluded: DupelessSeq[CandidatePosition],
                                      candidateTransferRows: Vector[DistributionOfPreferencesRow]
                                     ): DupelessSeq[CandidatePosition] = {
    val allExcludedCandidatesAtThisStep = candidateTransferRows.toStream
      .filter(_.status == excludedStatus)
      .map(row => candidatePositionLookup(row.ballotPosition))

    previouslyExcluded ++ allExcludedCandidatesAtThisStep
  }

  private def newlyElectedCandidatesIn(candidatePositionLookup: Map[Int, CandidatePosition],
                                       candidateTransferRows: Vector[DistributionOfPreferencesRow]
                                      ): DupelessSeq[CandidatePosition] = {
    val newlyElectedCandidates = candidateTransferRows.toStream
      .filter(_.changed contains true)
      .filter(_.orderElected != 0)
      .sortBy(_.orderElected)
      .map(row => candidatePositionLookup(row.ballotPosition))

    DupelessSeq(newlyElectedCandidates :_*)
  }

  private def newlyExcludedCandidateIn(candidatePositionLookup: Map[Int, CandidatePosition],
                                       candidateTransferRows: Vector[DistributionOfPreferencesRow]
                                      ): Option[CandidatePosition] = {
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
                                          outcomesToNow: CandidateStatuses[CandidatePosition],
                                          parsedCountStepData: ParsedCountStepData
                                         ): CandidateStatuses[CandidatePosition] = {
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

  private final case class ParsedCountStepData(totalFormalPapers: NumPapers,
                                               quota: NumVotes,
                                               distributionComment: String,
                                               candidateTransfers: Map[CandidatePosition, ParsedCountStepTransfer],
                                               allElectedCandidates: DupelessSeq[CandidatePosition],
                                               allExcludedCandidates: DupelessSeq[CandidatePosition],
                                               exhaustedTransfers: ParsedCountStepTransfer,
                                               gainLossTransfers: ParsedCountStepTransfer,
                                               candidatesElectedThisStep: DupelessSeq[CandidatePosition],
                                               candidateExcludedThisStep: Option[CandidatePosition]) {
    def candidateVoteCountsGiven(
                                  previousCandidateVoteCounts: CandidateVoteCounts[CandidatePosition]
                                ): CandidateVoteCounts[CandidatePosition] =
      CandidateVoteCounts(
        perCandidate = candidateTransfers.map { case (candidate, transfer) =>
          candidate -> transfer.asVoteCountGiven(previousCandidateVoteCounts.perCandidate(candidate))
        },
        exhausted = exhaustedTransfers.asVoteCountGiven(previousCandidateVoteCounts.exhausted),
        roundingError = gainLossTransfers.asVoteCountGiven(previousCandidateVoteCounts.roundingError),
      )

    def candidateVoteCountsGivenNoPrevious: CandidateVoteCounts[CandidatePosition] = {
      CandidateVoteCounts(
        perCandidate = candidateTransfers.mapValues { transfer =>
          transfer.asVoteCountGiven(previousVoteCount = VoteCount.zero)
        },
        exhausted = exhaustedTransfers.asVoteCountGiven(VoteCount.zero),
        roundingError = gainLossTransfers.asVoteCountGiven(VoteCount.zero),
      )
    }
  }

  private final case class InitialPositionAndMetadata(initialAllocation: InitialAllocation[CandidatePosition],
                                                      allocationAfterIneligibles: AllocationAfterIneligibles[CandidatePosition],
                                                      nextDistributionSource: Option[DistributionCountStep.Source[CandidatePosition]],
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
