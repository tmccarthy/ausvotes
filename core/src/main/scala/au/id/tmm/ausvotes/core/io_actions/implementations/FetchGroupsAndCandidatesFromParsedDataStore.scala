package au.id.tmm.ausvotes.core.io_actions.implementations

import au.id.tmm.ausvotes.core.engine.ParsedDataStore
import au.id.tmm.ausvotes.core.io_actions.FetchSenateGroupsAndCandidates
import au.id.tmm.ausvotes.model.federal.senate.{SenateGroupsAndCandidates, SenateElection}
import scalaz.zio.IO

final class FetchGroupsAndCandidatesFromParsedDataStore(
                                                         parsedDataStore: ParsedDataStore,
                                                       ) extends FetchSenateGroupsAndCandidates[IO] {
  override def fetchGroupsAndCandidatesFor(senateElection: SenateElection): IO[FetchSenateGroupsAndCandidates.Error, SenateGroupsAndCandidates] =
    IO.syncException {
      parsedDataStore.groupsAndCandidatesFor(senateElection)
    }.leftMap(FetchSenateGroupsAndCandidates.Error)
}
