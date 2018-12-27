package au.id.tmm.ausvotes.model

import au.id.tmm.ausvotes.model.VoteCollectionPoint.PollingPlace.Location
import au.id.tmm.utilities.geo.LatLong
import au.id.tmm.utilities.geo.australia.Address

sealed trait VoteCollectionPoint[E, J] {
  def election: E
  def jurisdiction: J
}

object VoteCollectionPoint {

  // TODO tests for this once the fixtures are ported over
  implicit def ordering[E : Ordering, J : Ordering]: Ordering[VoteCollectionPoint[E, J]] = {
    val orderingUpToJurisdiction = Ordering.by((vcp: VoteCollectionPoint[E, J]) => (vcp.election, vcp.jurisdiction))

    (left, right) => {
      orderingUpToJurisdiction.compare(left, right) match {
        case 0 => {
          (left, right) match {
            case (PollingPlace(_, _, _, leftName, _), PollingPlace(_, _, _, rightName, _)) => leftName.compareTo(rightName)
            case (_: Special[E, J], _: PollingPlace[E, J]) => 1
            case (_: PollingPlace[E, J], _: Special[E, J]) => -1
            case (left: Special[E, J], right: Special[E, J]) => Special.ordering[E, J].compare(left, right)
          }
        }
        case x => x
      }
    }
  }

  final case class Special[E, J](
                                  election: E,
                                  jurisdiction: J,
                                  specialVcpType: Special.SpecialVcpType,
                                  id: Special.Id,
                                ) extends VoteCollectionPoint[E, J]

  object Special {

    implicit def ordering[E : Ordering, J : Ordering]: Ordering[Special[E, J]] =
      Ordering.by(s => (s.election, s.jurisdiction, s.specialVcpType, s.id.asInt))

    final case class Id(asInt: Int) extends AnyVal

    sealed trait SpecialVcpType

    object SpecialVcpType {
      case object Absentee extends SpecialVcpType
      case object Postal extends SpecialVcpType
      case object PrePoll extends SpecialVcpType
      case object Provisional extends SpecialVcpType

      implicit val ordering: Ordering[SpecialVcpType] = new Ordering[SpecialVcpType] {

        private def scoreOf(specialVcpType: SpecialVcpType): Int = specialVcpType match {
          case Absentee => 1
          case Postal => 2
          case PrePoll => 3
          case Provisional => 4
        }

        override def compare(left: SpecialVcpType, right: SpecialVcpType): Int = scoreOf(left) - scoreOf(right)
      }
    }
  }

  final case class PollingPlace[E, J](
                                       election: E,
                                       jurisdiction: J,
                                       id: PollingPlace.Id,
                                       name: String,
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
