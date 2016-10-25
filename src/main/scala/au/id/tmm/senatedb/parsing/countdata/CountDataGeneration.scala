package au.id.tmm.senatedb.parsing.countdata

import au.id.tmm.senatedb.model.CountStep.CountStepTransfer
import au.id.tmm.senatedb.model.parsing.CandidatePosition
import au.id.tmm.senatedb.model.parsing.CandidatePosition.constructBallotPositionLookup
import au.id.tmm.senatedb.model.{CountData, CountStep, GroupsAndCandidates, SenateElection}
import au.id.tmm.senatedb.rawdata.model.DistributionOfPreferencesRow
import au.id.tmm.utilities.collection.IteratorUtils.ImprovedIterator
import au.id.tmm.utilities.collection.OrderedSet
import au.id.tmm.utilities.geo.australia.State

object CountDataGeneration {

  private val ballotPositionForExhausted = 1001
  private val ballotPositionForGainLoss = 1002

  private val specialBallotPositions = Set(ballotPositionForExhausted, ballotPositionForGainLoss)

  def fromDistributionOfPreferencesRows(election: SenateElection,
                                        state: State,
                                        allGroupsAndCandidates: GroupsAndCandidates,
                                        distributionOfPreferencesRows: Iterator[DistributionOfPreferencesRow]
                                       ): CountData = {
    val groupsAndCandidates = allGroupsAndCandidates.findFor(election, state)
    val candidatePositionLookup = constructBallotPositionLookup(groupsAndCandidates)

    val initialAllocation = readInitialAllocation(distributionOfPreferencesRows, candidatePositionLookup)

    ???
  }

  private def readInitialAllocation(rows: Iterator[DistributionOfPreferencesRow],
                                    candidatePositionLookup: Map[Int, CandidatePosition]
                                   ): (CountStep.InitialAllocation, Option[CountStep.DistributionSource], Long) = {
    val (candidateTransferRows, exhaustedBallotsRow, gainLossRow) = readRowsForNextCount(rows, candidatePositionLookup)

    val totalFormalPapers = candidateTransferRows.head.totalFormalPapers
    val distributionComment = candidateTransferRows.last.comment

    val candidateTransfers = candidateTransferRows
      .map(row => candidatePositionLookup(row.ballotPosition) -> transfersFrom(row))
      .toMap

    val electedCandidates = electedCandidatesOf(candidatePositionLookup, candidateTransferRows)

    val exhaustedTransfers = transfersFrom(exhaustedBallotsRow)

    val gainLossTransfers = transfersFrom(gainLossRow)

    val initialAllocation = CountStep.InitialAllocation(
      candidateTransfers, exhaustedTransfers, gainLossTransfers, electedCandidates, OrderedSet.empty
    )

    val nextStepDistributionSource = distributionSourceFrom(distributionComment, Vector.empty)

    (initialAllocation, nextStepDistributionSource, totalFormalPapers)
  }

  private def readRowsForNextCount(rows: Iterator[DistributionOfPreferencesRow],
                           candidatePositionLookup: Map[Int, CandidatePosition]
                          ): (Vector[DistributionOfPreferencesRow], DistributionOfPreferencesRow, DistributionOfPreferencesRow) = {
    val initialCountRows = rows.readUntil(row => row.ballotPosition == ballotPositionForGainLoss)
    assert(initialCountRows.forall(_.count == 1))

    val candidateTransferRows = initialCountRows.filterNot(row => specialBallotPositions.contains(row.ballotPosition))
    assert(candidateTransferRows.size == candidatePositionLookup.size)

    val exhaustedBallotsRow = initialCountRows(candidateTransferRows.size)
    assert(exhaustedBallotsRow.ballotPosition == ballotPositionForExhausted)

    val gainLossRow = initialCountRows(candidateTransferRows.size + 1)
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

  private def distributionSourceFrom(distributionComment: String,
                                     preceedingCountSteps: Vector[CountStep]
                                    ): Option[CountStep.DistributionSource] = ???

  private def electedCandidatesOf(candidatePositionLookup: Map[Int, CandidatePosition],
                                  candidateTransferRows: Vector[DistributionOfPreferencesRow]
                                 ): OrderedSet[CandidatePosition] = {
    val candidatesInOrderElected = candidateTransferRows.toStream
      .filter(_.orderElected != 0)
      .sortBy(_.orderElected)
      .map(row => candidatePositionLookup(row.ballotPosition))

    OrderedSet(candidatesInOrderElected :_*)
  }
}
