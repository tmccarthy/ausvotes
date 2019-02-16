package au.id.tmm.ausvotes.data_sources.aec.federal.parsed

import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, SenateGroupsAndCandidates}

trait FetchSenateGroupsAndCandidates[F[+_, +_]] {

  def senateGroupsAndCandidatesFor(election: SenateElection): F[FetchSenateGroupsAndCandidates.Error, SenateGroupsAndCandidates]

}

object FetchSenateGroupsAndCandidates {

  def senateGroupsAndCandidatesFor[F[+_, +_] : FetchSenateGroupsAndCandidates](election: SenateElection): F[FetchSenateGroupsAndCandidates.Error, SenateGroupsAndCandidates] =
    implicitly[FetchSenateGroupsAndCandidates[F]].senateGroupsAndCandidatesFor(election)

  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

}
