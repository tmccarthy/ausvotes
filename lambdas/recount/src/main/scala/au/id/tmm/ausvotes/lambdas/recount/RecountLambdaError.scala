package au.id.tmm.ausvotes.lambdas.recount

sealed trait RecountLambdaError

object RecountLambdaError {

  sealed trait WithException extends RecountLambdaError {
    def exception: Exception
  }

  case object RecountDataBucketUndefined extends RecountLambdaError

  sealed trait RecountRequestError extends RecountLambdaError

  object RecountRequestError {
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

  final case class WriteRecountError(cause: Exception) extends RecountLambdaError

}
