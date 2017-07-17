package au.id.tmm.senatedb.core.tallies

import au.id.tmm.senatedb.core.computations.BallotWithFacts
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.computation.SavingsProvision
import au.id.tmm.senatedb.core.model.parsing._
import au.id.tmm.utilities.geo.australia.State

trait BallotGrouping[A] {

  def groupsOf(ballotWithFacts: BallotWithFacts): Set[A]

}

object BallotGrouping {
  trait SingletonBallotGrouping[A] extends BallotGrouping[A] {
    override def groupsOf(ballotWithFacts: BallotWithFacts): Set[A] = Set(groupOf(ballotWithFacts))

    def groupOf(facts: BallotWithFacts): A
  }

  case object SenateElection extends SingletonBallotGrouping[SenateElection] {
    override def groupOf(ballotWithFacts: BallotWithFacts): SenateElection =
      ballotWithFacts.ballot.election
  }

  case object State extends SingletonBallotGrouping[State] {
    override def groupOf(ballotWithFacts: BallotWithFacts): State =
      ballotWithFacts.ballot.state
  }

  case object Division extends SingletonBallotGrouping[Division] {
    override def groupOf(ballotWithFacts: BallotWithFacts): Division =
      ballotWithFacts.ballot.division
  }

  case object VoteCollectionPoint extends SingletonBallotGrouping[VoteCollectionPoint] {
    override def groupOf(ballotWithFacts: BallotWithFacts): VoteCollectionPoint =
      ballotWithFacts.ballot.voteCollectionPoint
  }

  case object FirstPreferencedPartyNationalEquivalent extends SingletonBallotGrouping[Party] {
    override def groupOf(ballotWithFacts: BallotWithFacts): Party = ballotWithFacts.firstPreference.party.nationalEquivalent
  }

  case object FirstPreferencedParty extends SingletonBallotGrouping[Party] {
    override def groupOf(ballotWithFacts: BallotWithFacts): Party = ballotWithFacts.firstPreference.party
  }

  case object FirstPreferencedGroup extends SingletonBallotGrouping[BallotGroup] {
    override def groupOf(ballotWithFacts: BallotWithFacts): BallotGroup = ballotWithFacts.firstPreference.group
  }

  case object UsedSavingsProvision extends BallotGrouping[SavingsProvision] {
    override def groupsOf(ballotWithFacts: BallotWithFacts): Set[SavingsProvision] = ballotWithFacts.savingsProvisionsUsed
  }
}