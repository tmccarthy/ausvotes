package au.id.tmm.ausvotes.core.io_actions

import au.id.tmm.ausvotes.core.model.GroupsAndCandidates
import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.ausvotes.model.federal.senate.SenateElection

trait FetchSenateGroupsAndCandidates[F[+_, +_]] {

  def fetchGroupsAndCandidatesFor(senateElection: SenateElection): F[FetchSenateGroupsAndCandidates.Error, GroupsAndCandidates]

}

object FetchSenateGroupsAndCandidates {

  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

  def fetchFor[F[+_, +_] : FetchSenateGroupsAndCandidates](senateElection: SenateElection): F[FetchSenateGroupsAndCandidates.Error, GroupsAndCandidates] =
    implicitly[FetchSenateGroupsAndCandidates[F]].fetchGroupsAndCandidatesFor(senateElection)

}
