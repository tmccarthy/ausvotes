package au.id.tmm.ausvotes.api.errors.recount

import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import au.id.tmm.ausvotes.shared.recountresources.RecountRequest

sealed abstract class RecountException extends ExceptionCaseClass

object RecountException {
  final case class BadRequestError(cause: RecountRequest.Error) extends RecountException
  final case class CheckRecountComputedError(cause: Exception) extends RecountException with ExceptionCaseClass.WithCause
  final case class RequestRecountError(cause: Exception) extends RecountException with ExceptionCaseClass.WithCause
}
