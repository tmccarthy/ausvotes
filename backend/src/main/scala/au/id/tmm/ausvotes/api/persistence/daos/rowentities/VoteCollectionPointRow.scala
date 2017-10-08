package au.id.tmm.ausvotes.api.persistence.daos.rowentities

import au.id.tmm.ausvotes.core.model.parsing.{PollingPlace, VoteCollectionPoint}

private[daos] trait VoteCollectionPointRow {
  def asVoteCollectionPoint: VoteCollectionPoint
}

private[daos] object VoteCollectionPointRow {

  sealed trait VcpType

  object VcpType {
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