package au.id.tmm.ausvotes.data_sources.nswec.raw

import au.id.tmm.ausvotes.model.nsw.legco.{NswLegCoElection, _}

trait FetchRawLegCoGroupsAndCandidates[F[+_, +_]] {
  def groupAndCandidateRowsFor(nswLegCoElection: NswLegCoElection): F[Exception, GroupsAndCandidates]
}
