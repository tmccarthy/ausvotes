package au.id.tmm.ausvotes.model.nsw

sealed trait NswVoteCollectionPoint {
  val election: NswElection
  val district: District
}

object NswVoteCollectionPoint {
  final case class PollingPlace(election: NswElection, district: District, name: String, pollingPlaceType: PollingPlace.Type) extends NswVoteCollectionPoint

  object PollingPlace {
    sealed trait Type

    object Type {
      case object VotingCentre extends Type
      case object EarlyVotingCentre extends Type
      case object DeclarationVote extends Type
    }
  }

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
