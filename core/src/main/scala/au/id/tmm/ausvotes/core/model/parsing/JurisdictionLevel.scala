package au.id.tmm.ausvotes.core.model.parsing

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.utilities.geo.australia.State

sealed trait JurisdictionLevel[+A] {

  def ofBallot(ballot: Ballot): A

}

object JurisdictionLevel {

  case object Nation extends JurisdictionLevel[SenateElection] {
    override def ofBallot(ballot: Ballot): SenateElection = ballot.election
  }

  case object State extends JurisdictionLevel[State] {
    override def ofBallot(ballot: Ballot): State = ballot.state
  }

  case object Division extends JurisdictionLevel[Division] {
    override def ofBallot(ballot: Ballot): Division = ballot.division
  }

  case object VoteCollectionPoint extends JurisdictionLevel[VoteCollectionPoint] {
    override def ofBallot(ballot: Ballot): VoteCollectionPoint = ballot.voteCollectionPoint
  }

  val ALL: Set[JurisdictionLevel[Any]] = Set(Nation, State, Division, VoteCollectionPoint)
  val ALL_EXCEPT_NATION: Set[JurisdictionLevel[Any]] = ALL - Nation
}