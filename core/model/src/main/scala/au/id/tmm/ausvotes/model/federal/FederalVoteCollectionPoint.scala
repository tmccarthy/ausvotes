package au.id.tmm.ausvotes.model.federal

import au.id.tmm.ausvotes.model.federal.FederalVoteCollectionPoint.FederalPollingPlace.{Location, PollingPlaceType}
import au.id.tmm.ausgeo.LatLong
import au.id.tmm.ausgeo.{Address, State}

sealed trait FederalVoteCollectionPoint {
  def election: FederalElection
  def state: State
  def division: Division
  def name: String
}

object FederalVoteCollectionPoint {

  // TODO tests for this once the fixtures are ported over
  implicit val ordering: Ordering[FederalVoteCollectionPoint] = {
    val orderingUpToJurisdiction: Ordering[FederalVoteCollectionPoint] = Ordering.by((vcp: FederalVoteCollectionPoint) => (vcp.election, vcp.state, vcp.division))

    (left, right) => {
      orderingUpToJurisdiction.compare(left, right) match {
        case 0 => {
          (left, right) match {
            case (FederalPollingPlace(_, _, _, _, _, leftName, _), FederalPollingPlace(_, _, _, _, _, rightName, _)) => leftName.compareTo(rightName)
            case (_: Special, _: FederalPollingPlace) => 1
            case (_: FederalPollingPlace, _: Special) => -1
            case (left: Special, right: Special) => Special.ordering.compare(left, right)
          }
        }
        case x => x
      }
    }
  }

  final case class Special(
                            election: FederalElection,
                            state: State,
                            division: Division,
                            specialVcpType: Special.SpecialVcpType,
                            id: Special.Id,
                          ) extends FederalVoteCollectionPoint {
    override def name: String = s"${specialVcpType.toString.toUpperCase} ${id.asInt}"
  }

  object Special {

    implicit def ordering: Ordering[Special] =
      Ordering.by(s => (s.election, s.state, s.division, s.specialVcpType, s.id.asInt))

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

  final case class FederalPollingPlace(
                                        election: FederalElection,
                                        state: State,
                                        division: Division,
                                        id: FederalPollingPlace.Id,
                                        pollingPlaceType: PollingPlaceType,
                                        name: String,
                                        location: Location,
                                      ) extends FederalVoteCollectionPoint

  object FederalPollingPlace {
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
