package au.id.tmm.ausvotes.shared.recountresources.entities.actions

import au.id.tmm.ausvotes.core.model.parsing.{Candidate, Group}
import au.id.tmm.ausvotes.core.model.{GroupsAndCandidates, SenateElection}
import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchPreferenceTree.{FetchPreferenceTreeException, GroupsCandidatesAndPreferences}
import au.id.tmm.countstv.model.preferences.PreferenceTree.RootPreferenceTree
import au.id.tmm.utilities.geo.australia.State

trait FetchPreferenceTree[F[+_, +_]] {

  def fetchGroupsCandidatesAndPreferencesFor(
                                              election: SenateElection,
                                              state: State,
                                            ): F[FetchPreferenceTreeException, GroupsCandidatesAndPreferences]

  def useGroupsCandidatesAndPreferencesWhileCaching[E, A](
                                                           election: SenateElection,
                                                           state: State,
                                                         )(
                                                           handleEntityFetchError: FetchPreferenceTreeException => F[E, A],
                                                           handleCachePopulationError: FetchPreferenceTreeException => F[E, Unit],
                                                         )(
                                                           action: GroupsCandidatesAndPreferences => F[E, A],
                                                         ): F[E, A]

}

object FetchPreferenceTree {

  def fetchGroupsCandidatesAndPreferencesFor[F[+_, +_] : FetchPreferenceTree](
                                                                               election: SenateElection,
                                                                               state: State,
                                                                             ): F[FetchPreferenceTreeException, GroupsCandidatesAndPreferences] =
    implicitly[FetchPreferenceTree[F]].fetchGroupsCandidatesAndPreferencesFor(election, state)

  def useGroupsCandidatesAndPreferencesWhileCaching[F[+_, +_] : FetchPreferenceTree, E, A](
                                                                                            election: SenateElection,
                                                                                            state: State,
                                                                                          )(
                                                                                            handleEntityFetchError: FetchPreferenceTreeException => F[E, A],
                                                                                            handleCachePopulationError: FetchPreferenceTreeException => F[E, Unit],
                                                                                          )(
                                                                                            action: GroupsCandidatesAndPreferences => F[E, A],
                                                                                          ): F[E, A] =
    implicitly[FetchPreferenceTree[F]].useGroupsCandidatesAndPreferencesWhileCaching(election, state)(handleEntityFetchError, handleCachePopulationError)(action)

  sealed abstract class FetchPreferenceTreeException extends ExceptionCaseClass

  object FetchPreferenceTreeException {
    final case class FetchGroupsAndCandidatesException(cause: FetchGroupsAndCandidates.FetchGroupsAndCandidatesException) extends FetchPreferenceTreeException with ExceptionCaseClass.WithCause
    final case class LoadBytesExceptionFetch(cause: Exception) extends FetchPreferenceTreeException with ExceptionCaseClass.WithCause
    final case class DeserialisationFetchPreferenceTreeException(cause: Exception) extends FetchPreferenceTreeException with ExceptionCaseClass.WithCause
  }

  final case class GroupsCandidatesAndPreferences(
                                                   groupsAndCandidates: GroupsAndCandidates,
                                                   preferenceTree: RootPreferenceTree[Candidate],
                                                 ) {
    def groups: Set[Group] = groupsAndCandidates.groups
    def candidates: Set[Candidate] = groupsAndCandidates.candidates
  }
}
