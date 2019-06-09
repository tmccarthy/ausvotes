package au.id.tmm.ausvotes.data_sources.nswec.parsed

import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.ausvotes.model.nsw.legco.{NswLegCoElection, _}

trait FetchNswLegCoGroupsAndCandidates[F[+_, +_]] {
  def groupsAndCandidatesFor(election: NswLegCoElection): F[FetchNswLegCoGroupsAndCandidates.Error, GroupsAndCandidates]
}

object FetchNswLegCoGroupsAndCandidates {
  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause
}
