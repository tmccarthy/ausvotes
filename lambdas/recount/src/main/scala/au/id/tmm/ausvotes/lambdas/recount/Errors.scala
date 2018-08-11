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

}
