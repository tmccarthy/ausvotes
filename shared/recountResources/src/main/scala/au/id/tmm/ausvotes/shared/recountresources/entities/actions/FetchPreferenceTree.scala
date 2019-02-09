package au.id.tmm.ausvotes.shared.recountresources.entities.actions

import au.id.tmm.ausvotes.model.federal.senate.{SenateGroupsAndCandidates, SenateCandidate, SenateElectionForState, SenateGroup}
import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchPreferenceTree.{FetchPreferenceTreeException, GroupsCandidatesAndPreferences}
import au.id.tmm.countstv.model.preferences.PreferenceTree.RootPreferenceTree

trait FetchPreferenceTree[F[+_, +_]] {

  def fetchGroupsCandidatesAndPreferencesFor(
                                              election: SenateElectionForState,
                                            ): F[FetchPreferenceTreeException, GroupsCandidatesAndPreferences]

  def useGroupsCandidatesAndPreferencesWhileCaching[E, A](
                                                           election: SenateElectionForState,
                                                         )(
                                                           handleEntityFetchError: FetchPreferenceTreeException => F[E, A],
                                                           handleCachePopulationError: FetchPreferenceTreeException => F[E, Unit],
                                                         )(
                                                           action: GroupsCandidatesAndPreferences => F[E, A],
                                                         ): F[E, A]

}

object FetchPreferenceTree {

  def fetchGroupsCandidatesAndPreferencesFor[F[+_, +_] : FetchPreferenceTree](
                                                                               election: SenateElectionForState,
                                                                             ): F[FetchPreferenceTreeException, GroupsCandidatesAndPreferences] =
    implicitly[FetchPreferenceTree[F]].fetchGroupsCandidatesAndPreferencesFor(election)

  def useGroupsCandidatesAndPreferencesWhileCaching[F[+_, +_] : FetchPreferenceTree, E, A](
                                                                                            election: SenateElectionForState,
                                                                                          )(
                                                                                            handleEntityFetchError: FetchPreferenceTreeException => F[E, A],
                                                                                            handleCachePopulationError: FetchPreferenceTreeException => F[E, Unit],
                                                                                          )(
                                                                                            action: GroupsCandidatesAndPreferences => F[E, A],
                                                                                          ): F[E, A] =
    implicitly[FetchPreferenceTree[F]].useGroupsCandidatesAndPreferencesWhileCaching(election)(handleEntityFetchError, handleCachePopulationError)(action)

  sealed abstract class FetchPreferenceTreeException extends ExceptionCaseClass

  object FetchPreferenceTreeException {
    final case class FetchGroupsAndCandidatesException(cause: FetchGroupsAndCandidates.FetchGroupsAndCandidatesException) extends FetchPreferenceTreeException with ExceptionCaseClass.WithCause
    final case class LoadBytesExceptionFetch(cause: Exception) extends FetchPreferenceTreeException with ExceptionCaseClass.WithCause
    final case class DeserialisationFetchPreferenceTreeException(cause: Exception) extends FetchPreferenceTreeException with ExceptionCaseClass.WithCause
  }

  final case class GroupsCandidatesAndPreferences(
                                                   groupsAndCandidates: SenateGroupsAndCandidates,
                                                   preferenceTree: RootPreferenceTree[SenateCandidate],
                                                 ) {
    def groups: Set[SenateGroup] = groupsAndCandidates.groups
    def candidates: Set[SenateCandidate] = groupsAndCandidates.candidates
  }
}
