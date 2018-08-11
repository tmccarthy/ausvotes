package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.utilities.geo.australia.State

object Errors {

  sealed trait RecountLambdaError

  sealed trait RecountRequestError extends RecountLambdaError

  object RecountRequestError {
    case object MissingElection extends RecountRequestError
    final case class InvalidElectionId(badElectionId: String) extends RecountRequestError

    case object MissingState extends RecountRequestError
    final case class InvalidStateId(badStateId: String) extends RecountRequestError
    final case class NoElectionForState(election: SenateElection, state: State) extends RecountRequestError

    final case class InvalidNumVacancies(badNumVacancies: String) extends RecountRequestError
  }

  sealed trait EntityFetchError extends RecountLambdaError

  object EntityFetchError {
    final case class GroupFetchError(exception: Exception) extends EntityFetchError
    final case class GroupDecodeError(message: String) extends EntityFetchError

    final case class CandidateFetchError(exception: Exception) extends EntityFetchError
    final case class CandidateDecodeError(message: String) extends EntityFetchError

    final case class PreferenceTreeFetchError(exception: Exception) extends EntityFetchError
  }

}
