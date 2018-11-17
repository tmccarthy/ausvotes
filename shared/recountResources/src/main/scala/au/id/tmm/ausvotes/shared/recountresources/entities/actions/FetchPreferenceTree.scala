package au.id.tmm.ausvotes.shared.recountresources.entities.actions

import au.id.tmm.ausvotes.core.model.parsing.{Candidate, CandidatePosition, Group}
import au.id.tmm.ausvotes.core.model.{GroupsAndCandidates, SenateElection}
import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchPreferenceTree.{GroupsCandidatesAndPreferences, PreferenceTreeCacheException}
import au.id.tmm.countstv.model.preferences.PreferenceTree.RootPreferenceTree
import au.id.tmm.utilities.geo.australia.State

abstract class FetchPreferenceTree[F[_, _]] {

  def fetchGroupsCandidatesAndPreferencesFor(
                                              election: SenateElection,
                                              state: State,
                                            ): F[PreferenceTreeCacheException, GroupsCandidatesAndPreferences]

}

object FetchPreferenceTree {
  sealed abstract class PreferenceTreeCacheException extends ExceptionCaseClass

  object PreferenceTreeCacheException {
    final case class FetchGroupsAndCandidatesException(cause: FetchGroupsAndCandidates.FetchGroupsAndCandidatesException) extends PreferenceTreeCacheException with ExceptionCaseClass.WithCause
    final case class LoadBytesException(cause: Exception) extends PreferenceTreeCacheException with ExceptionCaseClass.WithCause
    final case class DeserialisationPreferenceTreeException(cause: Exception) extends PreferenceTreeCacheException with ExceptionCaseClass.WithCause
  }

  final case class GroupsCandidatesAndPreferences(
                                                   groupsAndCandidates: GroupsAndCandidates,
                                                   preferenceTree: RootPreferenceTree[CandidatePosition],
                                                 ) {
    def groups: Set[Group] = groupsAndCandidates.groups
    def candidates: Set[Candidate] = groupsAndCandidates.candidates
  }
}
