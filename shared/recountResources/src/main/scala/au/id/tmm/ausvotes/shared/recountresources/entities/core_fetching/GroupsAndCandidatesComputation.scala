package au.id.tmm.ausvotes.shared.recountresources.entities.core_fetching

import au.id.tmm.ausvotes.core.engine.ParsedDataStore
import au.id.tmm.ausvotes.core.model.{GroupsAndCandidates, SenateElection}
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchGroupsAndCandidates
import au.id.tmm.utilities.geo.australia.State
import scalaz.zio.IO

class GroupsAndCandidatesComputation(
                                      parsedDataStore: ParsedDataStore,
                                    ) extends FetchGroupsAndCandidates[IO] {
  override def fetchGroupsAndCandidatesFor(
                                            election: SenateElection,
                                            state: State,
                                          ): IO[FetchGroupsAndCandidates.FetchGroupsAndCandidatesException, GroupsAndCandidates] =
    IO.syncException(parsedDataStore.groupsAndCandidatesFor(election).findFor(election, state))
      .leftMap(FetchGroupsAndCandidates.FetchGroupsAndCandidatesException.LoadGroupsJsonException) // TODO probably generalise the error type
}
