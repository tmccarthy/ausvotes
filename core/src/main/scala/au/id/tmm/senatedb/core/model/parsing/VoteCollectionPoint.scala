package au.id.tmm.senatedb.core.model.parsing

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.PollingPlace.PollingPlaceType
import au.id.tmm.utilities.geo.LatLong
import au.id.tmm.utilities.geo.australia.{Address, State}

sealed trait VoteCollectionPoint {
  def election: SenateElection
  def state: State
  def division: Division
  def name: String
}

object VoteCollectionPoint {

  sealed trait SpecialVoteCollectionPoint extends VoteCollectionPoint {
    def number: Int
  }

  final case class Absentee(election: SenateElection, state: State, division: Division, number: Int) extends SpecialVoteCollectionPoint {
    override val name = s"ABSENTEE $number"
  }

  final case class Postal(election: SenateElection, state: State, division: Division, number: Int) extends SpecialVoteCollectionPoint {
    override val name = s"POSTAL $number"
  }

  final case class PrePoll(election: SenateElection, state: State, division: Division, number: Int) extends SpecialVoteCollectionPoint {
    override val name = s"PRE-POLL $number"
  }

  final case class Provisional(election: SenateElection, state: State, division: Division, number: Int) extends SpecialVoteCollectionPoint {
    override val name = s"PROVISIONAL $number"
  }

  def addressOf(voteCollectionPoint: VoteCollectionPoint): Option[Address] = {
    voteCollectionPoint match {
      case p: PollingPlace => PollingPlace.Location.addressOf(p.location)
      case _ => None
    }
  }

}

final case class PollingPlace(election: SenateElection,
                              state: State,
                              division: Division,
                              aecId: Int,
                              pollingPlaceType: PollingPlaceType,
                              name: String,
                              location: PollingPlace.Location) extends VoteCollectionPoint {
}

object PollingPlace {
  sealed trait PollingPlaceType

  object PollingPlaceType {
    case object PollingPlace extends PollingPlaceType
    case object SpecialHospitalTeam extends PollingPlaceType
    case object RemoteMobileTeam extends PollingPlaceType
    case object OtherMobileTeam extends PollingPlaceType
    case object PrePollVotingCentre extends PollingPlaceType
  }

  sealed trait Location {
    def name: String
  }

  object Location {
    case object Multiple extends Location {
      val name: String = "Multiple Locations"
    }

    final case class Premises(name: String,
                              address: Address,
                              location: LatLong) extends Location

    final case class PremisesMissingLatLong(name: String,
                                            address: Address) extends Location

    def addressOf(location: Location): Option[Address] = location match {
      case p: Premises => Some(p.address)
      case p: PremisesMissingLatLong => Some(p.address)
      case _ => None
    }
  }
}