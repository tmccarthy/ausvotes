package au.id.tmm.ausvotes.shared.recountresources.entities.actions

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import au.id.tmm.ausvotes.shared.recountresources.CountSummary
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchCanonicalCountSummary.FetchCanonicalCountResultException
import au.id.tmm.utilities.geo.australia.State

trait FetchCanonicalCountSummary[F[+_, +_]] {

  def fetchCanonicalCountResultFor(election: SenateElection, state: State): F[FetchCanonicalCountResultException, CountSummary]

}

object FetchCanonicalCountSummary {

  def fetchCanonicalCountResultFor[F[+_, +_] : FetchCanonicalCountSummary](election: SenateElection, state: State): F[FetchCanonicalCountResultException, CountSummary] =
    implicitly[FetchCanonicalCountSummary[F]].fetchCanonicalCountResultFor(election, state)

  sealed abstract class FetchCanonicalCountResultException extends ExceptionCaseClass

  object FetchCanonicalCountResultException {
    final case class LoadCanonicalRecountJsonException(cause: Exception) extends FetchCanonicalCountResultException with ExceptionCaseClass.WithCause
    final case class FetchGroupsAndCandidatesException(cause: FetchGroupsAndCandidates.FetchGroupsAndCandidatesException) extends FetchCanonicalCountResultException with ExceptionCaseClass.WithCause
    final case class DecodeCanonicalRecountJsonException(message: String) extends FetchCanonicalCountResultException
  }

}
