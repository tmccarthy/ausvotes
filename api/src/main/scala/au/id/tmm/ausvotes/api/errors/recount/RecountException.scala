package au.id.tmm.ausvotes.api.errors.recount

import au.id.tmm.ausvotes.api.model.recount.RecountApiRequest
import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchCanonicalCountSummary

sealed abstract class RecountException extends ExceptionCaseClass

object RecountException {
  final case class BadRequestError(cause: RecountApiRequest.ConstructionException) extends RecountException with ExceptionCaseClass.WithCause
  final case class FetchCanonicalCountError(cause: FetchCanonicalCountSummary.FetchCanonicalCountResultException) extends RecountException with ExceptionCaseClass.WithCause
  final case class CheckRecountComputedError(cause: Exception) extends RecountException with ExceptionCaseClass.WithCause
  final case class RequestRecountError(cause: Exception) extends RecountException with ExceptionCaseClass.WithCause
}
