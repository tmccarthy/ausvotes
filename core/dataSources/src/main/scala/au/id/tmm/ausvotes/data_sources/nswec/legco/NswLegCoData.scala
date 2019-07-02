package au.id.tmm.ausvotes.data_sources.nswec.legco

import au.id.tmm.ausvotes.model.nsw.legco._
import fs2.Stream

trait NswLegCoData[F[+_, +_]] {

  def fetchGroupsAndCandidatesFor(election: NswLegCoElection): F[Exception, GroupsAndCandidates]

  def fetchPreferencesFor(
    election: NswLegCoElection,
    groupsAndCandidates: GroupsAndCandidates,
  ): F[Exception, Stream[F[Throwable, +?], Ballot]]

}
