package au.id.tmm.ausvotes.data_sources.aec.federal.parsed

import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.ausvotes.model.federal.senate.{SenateCandidate, SenateElection, SenateGroup}

trait FetchSenateGroupsAndCandidates[F[+_, +_]] {

  def senateGroupsAndCandidatesFor(election: SenateElection): F[FetchSenateGroupsAndCandidates.Error, FetchSenateGroupsAndCandidates.SenateGroupsAndCandidates]

}

object FetchSenateGroupsAndCandidates {

  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

  final case class SenateGroupsAndCandidates(
                                              groups: Set[SenateGroup],
                                              candidates: Set[SenateCandidate],
                                            )

}
