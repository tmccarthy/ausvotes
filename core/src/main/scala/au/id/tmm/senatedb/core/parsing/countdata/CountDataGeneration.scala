package au.id.tmm.senatedb.core.parsing.countdata

import au.id.tmm.senatedb.core.model.CountData.CountOutcome
import au.id.tmm.senatedb.core.model.CountStep.{CountStepTransfer, DistributionSource, InitialAllocation}
import au.id.tmm.senatedb.core.model.parsing.CandidatePosition
import au.id.tmm.senatedb.core.model.parsing.CandidatePosition.constructBallotPositionLookup
import au.id.tmm.senatedb.core.model.{CountData, CountStep, GroupsAndCandidates, SenateElection}
import au.id.tmm.senatedb.core.rawdata.model.DistributionOfPreferencesRow
import au.id.tmm.utilities.collection.IteratorUtils.ImprovedIterator
import au.id.tmm.utilities.collection.OrderedSet
import au.id.tmm.utilities.geo.australia.State

import scala.annotation.tailrec

object CountDataGeneration {

  private val ballotPositionForExhausted = 1001
  private val ballotPositionForGainLoss = 1002
  private val excludedStatus = "Excluded"

  private val specialBallotPositions = Set(ballotPositionForExhausted, ballotPositionForGainLoss)

  private type CandidateOutcomes = Map[CandidatePosition, CountOutcome]

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

    val (distributionSteps, candidateOutcomes) = readDistributionSteps(
      initialAllocationAndMetadata.initialAllocation,
      Vector(),
      initialAllocationAndMetadata.candidateOutcomes,
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
      initialAllocationAndMetadata.initialAllocation,
      distributionSteps,
      candidateOutcomes
    )
  }

  private def readInitialAllocation(rows: Iterator[DistributionOfPreferencesRow],
                                    candidatePositionLookup: Map[Int, CandidatePosition],
                                    distributionSourceCalculator: DistributionSourceCalculator
                                   ): InitialAllocationAndMetadata = {

    val parsedCountStepData = parseNextCountStep(
      rows = rows,
      candidatePositionLookup = candidatePositionLookup,
      distributionSourceCalculator = distributionSourceCalculator,
      previouslyExcludedCandidates = OrderedSet(),
      expectedCount = 1
    )

    val initialAllocation = CountStep.InitialAllocation(
      parsedCountStepData.candidateTransfers,
      parsedCountStepData.exhaustedTransfers,
      parsedCountStepData.gainLossTransfers,
      parsedCountStepData.allElectedCandidates
    )

    val nextStepDistributionSource = distributionSourceCalculator.calculateFor(parsedCountStepData.distributionComment,
      Vector(initialAllocation))

    val emptyOutcomesMap = candidatePositionLookup.values.map(_ -> CountOutcome.Remainder).toMap
    val outcomesAfterFirstStep = accumulateCandidateOutcomes(1, emptyOutcomesMap, parsedCountStepData)

    InitialAllocationAndMetadata(
      initialAllocation,
      nextStepDistributionSource,
      parsedCountStepData.totalFormalPapers,
      parsedCountStepData.quota,
      outcomesAfterFirstStep
    )
  }

  @tailrec
  private def readDistributionSteps(initialAllocation: InitialAllocation,
                                    distributionsSoFar: Vector[CountStep.DistributionStep],
                                    outcomesSoFar: CandidateOutcomes,
                                    distributionSourceForFirstStep: Option[DistributionSource],
                                    candidatePositionLookup: Map[Int, CandidatePosition],
                                    distributionSourceCalculator: DistributionSourceCalculator,
                                    dopRows: Iterator[DistributionOfPreferencesRow]
                                   ): (Vector[CountStep.DistributionStep], CandidateOutcomes) = {
    if (distributionSourceForFirstStep.isEmpty) {
      return (distributionsSoFar, outcomesSoFar)
    }

    val precedingCountSteps: Vector[CountStep] = Vector(initialAllocation) ++ distributionsSoFar
    val count = precedingCountSteps.size + 1

    val parsedCountStepData = parseNextCountStep(
      dopRows,
      candidatePositionLookup,
      distributionSourceCalculator,
      previouslyExcludedCandidates = precedingCountSteps.last.excluded,
      count
    )

    val thisDistributionStep = CountStep.DistributionStep(
      count,
      distributionSourceForFirstStep.get,
      parsedCountStepData.candidateTransfers,
      parsedCountStepData.exhaustedTransfers,
      parsedCountStepData.gainLossTransfers,
      parsedCountStepData.candidatesElectedThisStep,
      parsedCountStepData.candidateExcludedThisStep,
      parsedCountStepData.allElectedCandidates,
      parsedCountStepData.allExcludedCandidates
    )

    val countStepsSoFar = distributionsSoFar :+ thisDistributionStep

    val candidateOutcomes = accumulateCandidateOutcomes(count, outcomesSoFar, parsedCountStepData)

    val nextStepDistributionSource = distributionSourceCalculator.calculateFor(parsedCountStepData.distributionComment,
      Vector(initialAllocation) ++ countStepsSoFar)

    readDistributionSteps(
      initialAllocation,
      countStepsSoFar,
      candidateOutcomes,
      nextStepDistributionSource,
      candidatePositionLookup,
      distributionSourceCalculator,
      dopRows
    )
  }

  private def parseNextCountStep(rows: Iterator[DistributionOfPreferencesRow],
                                 candidatePositionLookup: Map[Int, CandidatePosition],
                                 distributionSourceCalculator: DistributionSourceCalculator,
                                 previouslyExcludedCandidates: OrderedSet[CandidatePosition],
                                 expectedCount: Int
                                ): ParsedCountStepData = {
    val (candidateTransferRows, exhaustedBallotsRow, gainLossRow) =
      readRowsForNextCount(rows, candidatePositionLookup, expectedCount)

    val totalFormalPapers = candidateTransferRows.head.totalFormalPapers
    val quota = candidateTransferRows.head.quota
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
                                   expectedCount: Int
                                  ): (Vector[DistributionOfPreferencesRow], DistributionOfPreferencesRow, DistributionOfPreferencesRow) = {
    val countRows = rows.readUntil(row => row.ballotPosition == ballotPositionForGainLoss)
    assert(countRows.forall(_.count == expectedCount))

    val candidateTransferRows = countRows.filterNot(row => specialBallotPositions.contains(row.ballotPosition))
    assert(candidateTransferRows.size == candidatePositionLookup.size)

    val exhaustedBallotsRow = countRows(candidateTransferRows.size)
    assert(exhaustedBallotsRow.ballotPosition == ballotPositionForExhausted)

    val gainLossRow = countRows(candidateTransferRows.size + 1)
    assert(gainLossRow.ballotPosition == ballotPositionForGainLoss)

    (candidateTransferRows, exhaustedBallotsRow, gainLossRow)
  }

  private def transfersFrom(row: DistributionOfPreferencesRow): CountStepTransfer = {
    CountStepTransfer(
      row.papers,
      row.votesTransferred,
      row.progressiveVoteTotal
    )
  }

  private def allElectedCandidatesOf(candidatePositionLookup: Map[Int, CandidatePosition],
                                     candidateTransferRows: Vector[DistributionOfPreferencesRow]
                                    ): OrderedSet[CandidatePosition] = {
    val candidatesInOrderElected = candidateTransferRows.toStream
      .filter(_.orderElected != 0)
      .sortBy(_.orderElected)
      .map(row => candidatePositionLookup(row.ballotPosition))

    OrderedSet(candidatesInOrderElected :_*)
  }

  private def allExcludedCandidatesOf(candidatePositionLookup: Map[Int, CandidatePosition],
                                      previouslyExcluded: OrderedSet[CandidatePosition],
                                      candidateTransferRows: Vector[DistributionOfPreferencesRow]
                                      ): OrderedSet[CandidatePosition] = {
    val allExcludedCandidatesAtThisStep = candidateTransferRows.toStream
      .filter(_.status == excludedStatus)
      .map(row => candidatePositionLookup(row.ballotPosition))

    previouslyExcluded ++ allExcludedCandidatesAtThisStep
  }

  private def newlyElectedCandidatesIn(candidatePositionLookup: Map[Int, CandidatePosition],
                                       candidateTransferRows: Vector[DistributionOfPreferencesRow]
                                      ): OrderedSet[CandidatePosition] = {
    val newlyElectedCandidates = candidateTransferRows.toStream
      .filter(_.changed contains true)
      .filter(_.orderElected != 0)
      .sortBy(_.orderElected)
      .map(row => candidatePositionLookup(row.ballotPosition))

    OrderedSet(newlyElectedCandidates :_*)
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

  private def accumulateCandidateOutcomes(count: Int,
                                          outcomesToNow: CandidateOutcomes,
                                          parsedCountStepData: ParsedCountStepData
                                         ): CandidateOutcomes = {
    val outcomesIncludingNewlyElected = accumulateOutcomesForElectedCandidates(count, outcomesToNow, parsedCountStepData)
    val outcomesIncludingNewlyExcluded = accumulateOutcomesForExcludedCandidates(count, outcomesIncludingNewlyElected, parsedCountStepData)

    outcomesIncludingNewlyExcluded
  }

  private def accumulateOutcomesForElectedCandidates(count: Int,
                                                     outcomesToNow: CandidateOutcomes,
                                                     parsedCountStepData: ParsedCountStepData): CandidateOutcomes = {
    val candidatesAlreadyElected = outcomesToNow.filter {
      case (candidate, o: CountOutcome.Elected) => true
      case _ => false
    }

    val candidatesNewlyElected = parsedCountStepData.candidatesElectedThisStep

    val outcomePerNewlyElectedCandidate = candidatesNewlyElected.zipWithIndex
      .map {
        case(candidate, index) => candidate -> (index + candidatesAlreadyElected.size + 1)
      }
      .map {
        case(candidate, orderElected) => candidate -> CountOutcome.Elected(orderElected, count)
      }
      .toMap

    outcomesToNow.map {
      case (candidate, existingOutcome) => candidate -> outcomePerNewlyElectedCandidate.getOrElse(candidate, existingOutcome)
    }
  }

  private def accumulateOutcomesForExcludedCandidates(count: Int,
                                                      outcomesToNow: CandidateOutcomes,
                                                      parsedCountStepData: ParsedCountStepData): CandidateOutcomes = {
    parsedCountStepData.candidateExcludedThisStep.map { candidateNewlyExcluded =>
      val candidatesAlreadyExcluded = outcomesToNow.filter {
        case (candidate, o: CountOutcome.Excluded) => true
        case _ => false
      }

      val outcomeForNewlyExcluded = CountOutcome.Excluded(candidatesAlreadyExcluded.size + 1, count)

      outcomesToNow + (candidateNewlyExcluded -> outcomeForNewlyExcluded)
    } getOrElse outcomesToNow
  }

  private final case class ParsedCountStepData(totalFormalPapers: Long,
                                               quota: Long,
                                               distributionComment: String,
                                               candidateTransfers: Map[CandidatePosition, CountStepTransfer],
                                               allElectedCandidates: OrderedSet[CandidatePosition],
                                               allExcludedCandidates: OrderedSet[CandidatePosition],
                                               exhaustedTransfers: CountStepTransfer,
                                               gainLossTransfers: CountStepTransfer,
                                               candidatesElectedThisStep: OrderedSet[CandidatePosition],
                                               candidateExcludedThisStep: Option[CandidatePosition])

  private final case class InitialAllocationAndMetadata(initialAllocation: InitialAllocation,
                                                        nextDistributionSource: Option[DistributionSource],
                                                        totalFormalPapers: Long,
                                                        quota: Long,
                                                        candidateOutcomes: CandidateOutcomes)
}
