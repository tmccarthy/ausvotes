package au.id.tmm.senatedb.model.parsing

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.parsing.PollingPlace.Type.Type
import au.id.tmm.utilities.geo.LatLong
import au.id.tmm.utilities.geo.australia.{Address, State}

sealed trait VoteCollectionPoint {
  def election: SenateElection
  def state: State
  def division: Division
}

object VoteCollectionPoint {

  final case class Absentee(election: SenateElection, state: State, division: Division, number: Int) extends VoteCollectionPoint

  final case class Postal(election: SenateElection, state: State, division: Division, number: Int) extends VoteCollectionPoint

  final case class PrePoll(election: SenateElection, state: State, division: Division, number: Int) extends VoteCollectionPoint

  final case class Provisional(election: SenateElection, state: State, division: Division, number: Int) extends VoteCollectionPoint

}
final case class PollingPlace(election: SenateElection,
                              state: State,
                              division: Division,
                              aecId: Int,
                              pollingPlaceType: Type,
                              name: String,
                              location: PollingPlace.Location) extends VoteCollectionPoint {
}

object PollingPlace {
  object Type extends Enumeration {
    type Type = Value

    val POLLING_PLACE = Value(1)
    val SPECIAL_HOSPITAL_TEAM = Value(2)
    val REMOTE_MOBILE_TEAM = Value(3)
    val OTHER_MOBILE_TEAM = Value(4)
    val PRE_POLL_VOTING_CENTRE = Value(5)
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
  }
}