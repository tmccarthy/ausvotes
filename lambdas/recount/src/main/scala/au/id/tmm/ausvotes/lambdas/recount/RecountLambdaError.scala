package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.shared.recountresources.RecountRequest

sealed trait RecountLambdaError

object RecountLambdaError {

  sealed trait WithException extends RecountLambdaError {
    def exception: Exception
  }

  sealed trait ConfigurationError extends RecountLambdaError

  object ConfigurationError {
    case object RecountDataBucketUndefined extends ConfigurationError
  }

  sealed trait RecountRequestError extends RecountLambdaError

  object RecountRequestError {
    final case class RecountRequestParseError(cause: RecountRequest.Error) extends RecountRequestError

    final case class InvalidCandidateIds(invalidCandidateAecIds: Set[String]) extends RecountRequestError
  }

  sealed trait EntityFetchError extends RecountLambdaError

  object EntityFetchError {
    final case class GroupFetchError(exception: Exception) extends EntityFetchError with WithException
    final case class GroupDecodeError(message: String) extends EntityFetchError

    final case class CandidateFetchError(exception: Exception) extends EntityFetchError with WithException
    final case class CandidateDecodeError(message: String) extends EntityFetchError

    final case class PreferenceTreeFetchError(exception: Exception) extends EntityFetchError with WithException
  }

  final case class RecountComputationError(exception: Exception) extends RecountLambdaError with WithException

}
