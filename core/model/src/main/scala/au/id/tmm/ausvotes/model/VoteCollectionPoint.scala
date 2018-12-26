package au.id.tmm.ausvotes.model

import au.id.tmm.ausvotes.model.VoteCollectionPoint.PollingPlace.Location
import au.id.tmm.utilities.geo.LatLong
import au.id.tmm.utilities.geo.australia.Address

sealed trait VoteCollectionPoint[E, J] {
  def election: E
  def jurisdiction: J
}

object VoteCollectionPoint {

  final case class Special[E, J](
                                  election: E,
                                  jurisdiction: J,
                                  specialVcpType: Special.SpecialVcpType,
                                  id: Special.Id,
                                ) extends VoteCollectionPoint[E, J]

  object Special {
    final case class Id(asInt: Int) extends AnyVal

    sealed trait SpecialVcpType

    object SpecialVcpType {
      case object Absentee extends SpecialVcpType
      case object Postal extends SpecialVcpType
      case object PrePoll extends SpecialVcpType
      case object Provisional extends SpecialVcpType
    }
  }

  final case class PollingPlace[E, J](
                                       election: E,
                                       jurisdiction: J,
                                       id: PollingPlace.Id,
                                       location: Location,
                                     ) extends VoteCollectionPoint[E, J]

  object PollingPlace {
    final case class Id(asInt: Int) extends AnyVal

    sealed trait PollingPlaceType

    object PollingPlaceType {
      case object PollingPlace extends PollingPlaceType
      case object SpecialHospitalTeam extends PollingPlaceType
      case object RemoteMobileTeam extends PollingPlaceType
      case object OtherMobileTeam extends PollingPlaceType
      case object PrePollVotingCentre extends PollingPlaceType
    }

    sealed trait Location

    object Location {
      case object Multiple extends Location

      final case class Premises(name: String,
                                address: Address,
                                location: Option[LatLong]) extends Location
    }

  }


}
