package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.computations.StvBallotWithFacts
import au.id.tmm.ausvotes.core.computations.parties.PartyEquivalenceComputation
import au.id.tmm.ausvotes.core.model.computation.SavingsProvision
import au.id.tmm.ausvotes.model.Party
import au.id.tmm.ausvotes.model.federal.senate.{SenateBallotGroup, SenateElection}
import au.id.tmm.ausvotes.model.federal.{Division, FederalVcp}
import au.id.tmm.utilities.geo.australia.State

sealed trait BallotGrouping[A] {

  def groupsOf(ballotWithFacts: StvBallotWithFacts): Set[A]

  def name: String

}

object BallotGrouping {
  sealed trait SingletonBallotGrouping[A] extends BallotGrouping[A] {
    override def groupsOf(ballotWithFacts: StvBallotWithFacts): Set[A] = Set(groupOf(ballotWithFacts))

    def groupOf(facts: StvBallotWithFacts): A
  }

  case object SenateElection extends SingletonBallotGrouping[SenateElection] {
    override def groupOf(ballotWithFacts: StvBallotWithFacts): SenateElection =
      ballotWithFacts.ballot.election.election

    override val name: String = "election"
  }

  case object State extends SingletonBallotGrouping[State] {
    override def groupOf(ballotWithFacts: StvBallotWithFacts): State =
      ballotWithFacts.ballot.election.state

    override val name: String = "state"
  }

  case object Division extends SingletonBallotGrouping[Division] {
    override def groupOf(ballotWithFacts: StvBallotWithFacts): Division =
      ballotWithFacts.ballot.jurisdiction.electorate

    override val name: String = "division"
  }

  case object VoteCollectionPoint extends SingletonBallotGrouping[FederalVcp] {
    override def groupOf(ballotWithFacts: StvBallotWithFacts): FederalVcp =
      ballotWithFacts.ballot.jurisdiction.voteCollectionPoint

    override val name: String = "vcp"
  }

  case object FirstPreferencedPartyNationalEquivalent extends SingletonBallotGrouping[Option[Party]] {
    override def groupOf(ballotWithFacts: StvBallotWithFacts): Option[Party] =
      ballotWithFacts.firstPreference.party.map(PartyEquivalenceComputation.nationalEquivalentOf)

    override val name: String = "first-preferenced party (national)"
  }

  case object FirstPreferencedParty extends SingletonBallotGrouping[Option[Party]] {
    override def groupOf(ballotWithFacts: StvBallotWithFacts): Option[Party] = ballotWithFacts.firstPreference.party

    override val name: String = "first-preferenced party"
  }

  case object FirstPreferencedGroup extends SingletonBallotGrouping[SenateBallotGroup] {
    override def groupOf(ballotWithFacts: StvBallotWithFacts): SenateBallotGroup = ballotWithFacts.firstPreference.group

    override val name: String = "first-preferenced group"
  }

  case object UsedSavingsProvision extends BallotGrouping[SavingsProvision] {
    override def groupsOf(ballotWithFacts: StvBallotWithFacts): Set[SavingsProvision] = ballotWithFacts.savingsProvisionsUsed

    override val name: String = "used savings provision"
  }
}
