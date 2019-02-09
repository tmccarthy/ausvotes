package au.id.tmm.ausvotes.shared.recountresources.entities.core_fetching

import au.id.tmm.ausvotes.core.engine.ParsedDataStore
import au.id.tmm.ausvotes.model.federal.senate.{SenateGroupsAndCandidates, SenateElectionForState}
import au.id.tmm.ausvotes.shared.recountresources.entities.actions.FetchGroupsAndCandidates
import scalaz.zio.IO

class GroupsAndCandidatesComputation(
                                      parsedDataStore: ParsedDataStore,
                                    ) extends FetchGroupsAndCandidates[IO] {
  override def fetchGroupsAndCandidatesFor(
                                            election: SenateElectionForState,
                                          ): IO[FetchGroupsAndCandidates.FetchGroupsAndCandidatesException, SenateGroupsAndCandidates] =
    IO.syncException(parsedDataStore.groupsAndCandidatesFor(election.election).findFor(election))
      .leftMap(FetchGroupsAndCandidates.FetchGroupsAndCandidatesException.LoadGroupsJsonException) // TODO probably generalise the error type
}
