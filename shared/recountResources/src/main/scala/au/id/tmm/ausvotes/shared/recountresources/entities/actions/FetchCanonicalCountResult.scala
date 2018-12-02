package au.id.tmm.ausvotes.shared.recountresources.entities.actions

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.Candidate
import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchCanonicalCountResult.FetchCanonicalCountResultException
import au.id.tmm.countstv.model.CompletedCount
import au.id.tmm.utilities.geo.australia.State

trait FetchCanonicalCountResult[F[+_, +_]] {

  def fetchCanonicalCountResultFor(election: SenateElection, state: State): F[FetchCanonicalCountResultException, CompletedCount[Candidate]]

}

object FetchCanonicalCountResult {

  def fetchCanonicalCountResultFor[F[+_, +_] : FetchCanonicalCountResult](election: SenateElection, state: State): F[FetchCanonicalCountResultException, CompletedCount[Candidate]] =
    implicitly[FetchCanonicalCountResult[F]].fetchCanonicalCountResultFor(election, state)

  final case class FetchCanonicalCountResultException(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

}
