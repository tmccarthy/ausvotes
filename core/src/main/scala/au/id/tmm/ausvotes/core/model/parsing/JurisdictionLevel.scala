package au.id.tmm.ausvotes.core.model.parsing

import au.id.tmm.ausvotes.model.federal.senate.SenateBallot
import au.id.tmm.ausvotes.model.federal.{Division, FederalElection, FederalVcp}
import au.id.tmm.utilities.geo.australia.State

// TODO redesign this concept
sealed trait JurisdictionLevel[+A] {

  def ofBallot(ballot: SenateBallot): A

}

object JurisdictionLevel {

  case object Nation extends JurisdictionLevel[FederalElection] {
    override def ofBallot(ballot: SenateBallot): FederalElection = ballot.election.election.federalElection
  }

  case object State extends JurisdictionLevel[State] {
    override def ofBallot(ballot: SenateBallot): State = ballot.jurisdiction.state
  }

  case object Division extends JurisdictionLevel[Division] {
    override def ofBallot(ballot: SenateBallot): Division = ballot.jurisdiction.electorate
  }

  case object VoteCollectionPoint extends JurisdictionLevel[FederalVcp] {
    override def ofBallot(ballot: SenateBallot): FederalVcp = ballot.jurisdiction.voteCollectionPoint
  }

  val ALL: Set[JurisdictionLevel[Any]] = Set(Nation, State, Division, VoteCollectionPoint)
  val ALL_EXCEPT_NATION: Set[JurisdictionLevel[Any]] = ALL - Nation
}
