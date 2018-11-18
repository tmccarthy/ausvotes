package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.core.model.parsing.Candidate.AecCandidateId
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchPreferenceTree

sealed trait RecountLambdaError

object RecountLambdaError {

  sealed trait WithException extends RecountLambdaError {
    def exception: Exception
  }

  case object RecountDataBucketUndefined extends RecountLambdaError

  sealed trait RecountRequestError extends RecountLambdaError

  object RecountRequestError {
    final case class InvalidCandidateIds(invalidCandidateAecIds: Set[AecCandidateId]) extends RecountRequestError
  }

  final case class EntityFetchError(exception: FetchPreferenceTree.FetchPreferenceTreeException) extends RecountLambdaError with WithException
  final case class EntityCachePopulationError(exception: FetchPreferenceTree.FetchPreferenceTreeException) extends RecountLambdaError with WithException

  final case class RecountComputationError(exception: Exception) extends RecountLambdaError with WithException

  final case class WriteRecountError(cause: Exception) extends RecountLambdaError

}
