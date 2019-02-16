package au.id.tmm.ausvotes.shared.recountresources.entities.actions

import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.FetchSenateGroupsAndCandidates
import au.id.tmm.ausvotes.model.federal.senate.SenateElectionForState
import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import au.id.tmm.ausvotes.shared.recountresources.CountSummary
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchCanonicalCountSummary.FetchCanonicalCountSummaryException

trait FetchCanonicalCountSummary[F[+_, +_]] {

  def fetchCanonicalCountSummaryFor(election: SenateElectionForState): F[FetchCanonicalCountSummaryException, CountSummary]

}

object FetchCanonicalCountSummary {

  def fetchCanonicalCountResultFor[F[+_, +_] : FetchCanonicalCountSummary](election: SenateElectionForState): F[FetchCanonicalCountSummaryException, CountSummary] =
    implicitly[FetchCanonicalCountSummary[F]].fetchCanonicalCountSummaryFor(election)

  sealed abstract class FetchCanonicalCountSummaryException extends ExceptionCaseClass

  object FetchCanonicalCountSummaryException {
    final case class LoadCanonicalRecountJsonException(cause: Exception) extends FetchCanonicalCountSummaryException with ExceptionCaseClass.WithCause
    final case class FetchGroupsAndCandidatesException(cause: FetchSenateGroupsAndCandidates.Error) extends FetchCanonicalCountSummaryException with ExceptionCaseClass.WithCause
    final case class DecodeCanonicalRecountJsonException(message: String) extends FetchCanonicalCountSummaryException
  }

}
