package au.id.tmm.ausvotes.shared.recountresources.entities.actions

import au.id.tmm.ausvotes.model.federal.senate.{SenateCandidate, SenateElectionForState}
import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchCanonicalCountResult.FetchCanonicalCountResultException
import au.id.tmm.countstv.model.CompletedCount

trait FetchCanonicalCountResult[F[+_, +_]] {

  def fetchCanonicalCountResultFor(election: SenateElectionForState): F[FetchCanonicalCountResultException, CompletedCount[SenateCandidate]]

}

object FetchCanonicalCountResult {

  def fetchCanonicalCountResultFor[F[+_, +_] : FetchCanonicalCountResult](election: SenateElectionForState): F[FetchCanonicalCountResultException, CompletedCount[SenateCandidate]] =
    implicitly[FetchCanonicalCountResult[F]].fetchCanonicalCountResultFor(election)

  sealed abstract class FetchCanonicalCountResultException extends ExceptionCaseClass with ExceptionCaseClass.WithCause

  object FetchCanonicalCountResultException {
    final case class FetchGroupsAndCandidatesException(cause: FetchGroupsAndCandidates.FetchGroupsAndCandidatesException) extends FetchCanonicalCountResultException
    final case class BuildCanonicalRecountException(cause: Exception) extends FetchCanonicalCountResultException
  }

}
