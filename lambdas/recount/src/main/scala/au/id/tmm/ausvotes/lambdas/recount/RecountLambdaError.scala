package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import au.id.tmm.ausvotes.shared.recountresources.recount.RunRecount

abstract class RecountLambdaError extends ExceptionCaseClass

object RecountLambdaError {

  final case class RecountDataBucketUndefined() extends RecountLambdaError
  final case class RecountComputationError(cause: RunRecount.Error) extends RecountLambdaError with ExceptionCaseClass.WithCause
  final case class WriteRecountError(cause: Exception) extends RecountLambdaError with ExceptionCaseClass.WithCause

}
