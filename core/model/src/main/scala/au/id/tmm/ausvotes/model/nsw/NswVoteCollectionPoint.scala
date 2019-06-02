package au.id.tmm.ausvotes.model.nsw

sealed trait NswVoteCollectionPoint {
  val election: NswElection
  val district: District
}

object NswVoteCollectionPoint {
  final case class PollingPlace(election: NswElection, district: District, name: String) extends NswVoteCollectionPoint

  final case class Special(election: NswElection, district: District, specialVcpType: Special.Type) extends NswVoteCollectionPoint

  object Special {
    sealed trait Type

    object Type {
      case object IVote extends Type
      case object Absent extends Type
      case object Postal extends Type
      case object EnrolmentOrProvisional extends Type
      case object DeclaredFacility extends Type
    }
  }
}
