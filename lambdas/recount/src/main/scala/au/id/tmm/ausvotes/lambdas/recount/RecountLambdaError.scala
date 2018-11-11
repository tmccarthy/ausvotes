package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.shared.recountresources.entities.RecountEntityCache.RecountEntityCacheException

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

  final case class EntityFetchError(exception: RecountEntityCacheException) extends RecountLambdaError with WithException
  final case class EntityCachePopulationError(exception: RecountEntityCacheException) extends RecountLambdaError with WithException

  final case class RecountComputationError(exception: Exception) extends RecountLambdaError with WithException

  final case class WriteRecountError(cause: Exception) extends RecountLambdaError

}
