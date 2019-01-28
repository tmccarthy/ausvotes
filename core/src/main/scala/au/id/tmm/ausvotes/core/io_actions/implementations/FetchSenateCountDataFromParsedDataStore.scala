package au.id.tmm.ausvotes.core.io_actions.implementations

import au.id.tmm.ausvotes.core.engine.ParsedDataStore
import au.id.tmm.ausvotes.core.io_actions.FetchSenateCountData
import au.id.tmm.ausvotes.core.model.GroupsAndCandidates
import au.id.tmm.ausvotes.model.federal.senate.{SenateCountData, SenateElectionForState}
import scalaz.zio.IO

final class FetchSenateCountDataFromParsedDataStore(parsedDataStore: ParsedDataStore) extends FetchSenateCountData[IO] {
  override def fetchCountDataFor(
                                  election: SenateElectionForState,
                                  groupsAndCandidatesForSenateElectionInState: GroupsAndCandidates,
                                ): IO[FetchSenateCountData.Error, SenateCountData] =
    IO.syncException {
      parsedDataStore.countDataFor(election, groupsAndCandidatesForSenateElectionInState)
    }.leftMap(FetchSenateCountData.Error)
}
