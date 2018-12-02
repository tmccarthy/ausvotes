package au.id.tmm.ausvotes.shared.recountresources.entities.actions

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import au.id.tmm.ausvotes.shared.recountresources.CountSummary
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchCanonicalCountSummary.FetchCanonicalCountSummaryException
import au.id.tmm.utilities.geo.australia.State

trait FetchCanonicalCountSummary[F[+_, +_]] {

  def fetchCanonicalCountSummaryFor(election: SenateElection, state: State): F[FetchCanonicalCountSummaryException, CountSummary]

}

object FetchCanonicalCountSummary {

  def fetchCanonicalCountResultFor[F[+_, +_] : FetchCanonicalCountSummary](election: SenateElection, state: State): F[FetchCanonicalCountSummaryException, CountSummary] =
    implicitly[FetchCanonicalCountSummary[F]].fetchCanonicalCountSummaryFor(election, state)

  sealed abstract class FetchCanonicalCountSummaryException extends ExceptionCaseClass

  object FetchCanonicalCountSummaryException {
    final case class LoadCanonicalRecountJsonException(cause: Exception) extends FetchCanonicalCountSummaryException with ExceptionCaseClass.WithCause
    final case class FetchGroupsAndCandidatesException(cause: FetchGroupsAndCandidates.FetchGroupsAndCandidatesException) extends FetchCanonicalCountSummaryException with ExceptionCaseClass.WithCause
    final case class DecodeCanonicalRecountJsonException(message: String) extends FetchCanonicalCountSummaryException
  }

}
