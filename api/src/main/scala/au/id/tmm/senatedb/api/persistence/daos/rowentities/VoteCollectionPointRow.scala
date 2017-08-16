package au.id.tmm.senatedb.api.persistence.daos.rowentities

import au.id.tmm.senatedb.core.model.parsing.{PollingPlace, VoteCollectionPoint}

private[daos] trait VoteCollectionPointRow {
  def asVoteCollectionPoint: VoteCollectionPoint
}

private[daos] object VoteCollectionPointRow {

  sealed trait VcpType

  object VcpType {
    def parse(asString: String): VcpType = {
      asString match {
        case "absentee" => VcpType.Absentee
        case "postal" => VcpType.Postal
        case "prepoll" => VcpType.Prepoll
        case "provisional" => VcpType.Provisional
        case "polling_place" => VcpType.PollingPlace
      }
    }

    def asString(vcpType: VcpType): String = {
      vcpType match {
        case VcpType.Absentee => "absentee"
        case VcpType.Postal => "postal"
        case VcpType.Prepoll => "prepoll"
        case VcpType.Provisional => "provisional"
        case VcpType.PollingPlace => "polling_place"
      }
    }

    def of(voteCollectionPoint: VoteCollectionPoint): VcpType = {
      voteCollectionPoint match {
        case _: VoteCollectionPoint.Absentee => VcpType.Absentee
        case _: VoteCollectionPoint.Postal => VcpType.Postal
        case _: VoteCollectionPoint.PrePoll => VcpType.Prepoll
        case _: VoteCollectionPoint.Provisional => VcpType.Provisional
        case _: PollingPlace => VcpType.PollingPlace
      }
    }

    case object Absentee extends VcpType
    case object Postal extends VcpType
    case object Prepoll extends VcpType
    case object Provisional extends VcpType
    case object PollingPlace extends VcpType
  }
}