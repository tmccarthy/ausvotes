package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.computations.BallotWithFacts
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.computation.SavingsProvision
import au.id.tmm.ausvotes.core.model.parsing._
import au.id.tmm.utilities.geo.australia.State

trait BallotGrouping[A] {

  def groupsOf(ballotWithFacts: BallotWithFacts): Set[A]

  def name: String

}

object BallotGrouping {
  trait SingletonBallotGrouping[A] extends BallotGrouping[A] {
    override def groupsOf(ballotWithFacts: BallotWithFacts): Set[A] = Set(groupOf(ballotWithFacts))

    def groupOf(facts: BallotWithFacts): A
  }

  def ballotGroupingOf[A](jurisdictionLevel: JurisdictionLevel[A]): BallotGrouping[A] = {
    jurisdictionLevel match {
      case JurisdictionLevel.Nation => SenateElection.asInstanceOf[BallotGrouping[A]]
      case JurisdictionLevel.State => State.asInstanceOf[BallotGrouping[A]]
      case JurisdictionLevel.Division => Division.asInstanceOf[BallotGrouping[A]]
      case JurisdictionLevel.VoteCollectionPoint => VoteCollectionPoint.asInstanceOf[BallotGrouping[A]]
    }
  }

  case object SenateElection extends SingletonBallotGrouping[SenateElection] {
    override def groupOf(ballotWithFacts: BallotWithFacts): SenateElection =
      ballotWithFacts.ballot.election

    override val name: String = "election"
  }

  case object State extends SingletonBallotGrouping[State] {
    override def groupOf(ballotWithFacts: BallotWithFacts): State =
      ballotWithFacts.ballot.state

    override val name: String = "state"
  }

  case object Division extends SingletonBallotGrouping[Division] {
    override def groupOf(ballotWithFacts: BallotWithFacts): Division =
      ballotWithFacts.ballot.division

    override val name: String = "division"
  }

  case object VoteCollectionPoint extends SingletonBallotGrouping[VoteCollectionPoint] {
    override def groupOf(ballotWithFacts: BallotWithFacts): VoteCollectionPoint =
      ballotWithFacts.ballot.voteCollectionPoint

    override val name: String = "vcp"
  }

  case object FirstPreferencedPartyNationalEquivalent extends SingletonBallotGrouping[Party] {
    override def groupOf(ballotWithFacts: BallotWithFacts): Party = ballotWithFacts.firstPreference.party.nationalEquivalent

    override val name: String = "first-preferenced party (national)"
  }

  case object FirstPreferencedParty extends SingletonBallotGrouping[Party] {
    override def groupOf(ballotWithFacts: BallotWithFacts): Party = ballotWithFacts.firstPreference.party

    override val name: String = "first-preferenced party"
  }

  case object FirstPreferencedGroup extends SingletonBallotGrouping[BallotGroup] {
    override def groupOf(ballotWithFacts: BallotWithFacts): BallotGroup = ballotWithFacts.firstPreference.group

    override val name: String = "first-preferenced group"
  }

  case object UsedSavingsProvision extends BallotGrouping[SavingsProvision] {
    override def groupsOf(ballotWithFacts: BallotWithFacts): Set[SavingsProvision] = ballotWithFacts.savingsProvisionsUsed

    override val name: String = "used savings provision"
  }
}