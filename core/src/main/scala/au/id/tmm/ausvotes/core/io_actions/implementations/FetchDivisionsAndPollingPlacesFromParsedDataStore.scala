package au.id.tmm.ausvotes.core.io_actions.implementations

import au.id.tmm.ausvotes.core.engine.ParsedDataStore
import au.id.tmm.ausvotes.core.io_actions.FetchDivisionsAndPollingPlaces
import au.id.tmm.ausvotes.core.model.DivisionsAndPollingPlaces
import au.id.tmm.ausvotes.model.federal.FederalElection
import scalaz.zio.IO

final class FetchDivisionsAndPollingPlacesFromParsedDataStore(
                                                               parsedDataStore: ParsedDataStore,
                                                             ) extends FetchDivisionsAndPollingPlaces[IO] {
  override def fetchDivisionsAndPollingPlacesFor(federalElection: FederalElection): IO[FetchDivisionsAndPollingPlaces.Error, DivisionsAndPollingPlaces] =
    IO.syncException {
      parsedDataStore.divisionsAndPollingPlacesFor(federalElection)
    }.leftMap(FetchDivisionsAndPollingPlaces.Error)
}
