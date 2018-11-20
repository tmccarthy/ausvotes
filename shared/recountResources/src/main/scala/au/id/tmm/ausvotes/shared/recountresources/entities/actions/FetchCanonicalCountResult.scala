package au.id.tmm.ausvotes.shared.recountresources.entities.actions

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import au.id.tmm.ausvotes.shared.recountresources.RecountResult
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchCanonicalCountResult.FetchCanonicalCountResultException
import au.id.tmm.utilities.geo.australia.State

trait FetchCanonicalCountResult[F[+_, +_]] {

  def fetchCanonicalCountResultFor(election: SenateElection, state: State): F[FetchCanonicalCountResultException, RecountResult]

}

object FetchCanonicalCountResult {

  def fetchCanonicalCountResultFor[F[+_, +_] : FetchCanonicalCountResult](election: SenateElection, state: State): F[FetchCanonicalCountResultException, RecountResult] =
    implicitly[FetchCanonicalCountResult[F]].fetchCanonicalCountResultFor(election, state)

  sealed abstract class FetchCanonicalCountResultException extends ExceptionCaseClass

  object FetchCanonicalCountResultException {
    final case class LoadCanonicalRecountJsonException(cause: Exception) extends FetchCanonicalCountResultException with ExceptionCaseClass.WithCause
    final case class FetchGroupsAndCandidatesException(cause: FetchGroupsAndCandidates.FetchGroupsAndCandidatesException) extends FetchCanonicalCountResultException with ExceptionCaseClass.WithCause
    final case class DecodeCanonicalRecountJsonException(message: String) extends FetchCanonicalCountResultException
  }

}
