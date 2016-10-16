package au.id.tmm.senatedb.rawdata

import java.nio.file.Path

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.rawdata.csv.{ParsingDistributionOfPreferences, ParsingFirstPreferences, ParsingFormalPreferences, ParsingPollingPlaces}
import au.id.tmm.senatedb.rawdata.download.{LoadingDistributionsOfPreferences, LoadingFirstPreferences, LoadingFormalPreferences, LoadingPollingPlaces}
import au.id.tmm.senatedb.rawdata.model.{DistributionOfPreferencesRow, FirstPreferencesRow, FormalPreferencesRow, PollingPlacesRow}
import au.id.tmm.utilities.collection.CloseableIterator
import au.id.tmm.utilities.geo.australia.State

final class RawDataStore private (val location: Path) {
  def distributionsOfPreferencesFor(election: SenateElection, state: State): CloseableIterator[DistributionOfPreferencesRow] = {
    LoadingDistributionsOfPreferences.csvLinesOf(location, election, state)
      .flatMap(ParsingDistributionOfPreferences.parseLines)
      .get
  }

  def firstPreferencesFor(election: SenateElection): CloseableIterator[FirstPreferencesRow] = {
    LoadingFirstPreferences.csvLinesOf(location, election)
      .flatMap(ParsingFirstPreferences.parseLines)
      .get
  }

  def formalPreferencesFor(election: SenateElection, state: State): CloseableIterator[FormalPreferencesRow] = {
    LoadingFormalPreferences.csvLinesOf(location, election, state)
      .flatMap(ParsingFormalPreferences.parseLines)
      .get
  }

  def pollingPlacesFor(election: SenateElection): CloseableIterator[PollingPlacesRow] = {
    LoadingPollingPlaces.csvLinesOf(location, election)
      .flatMap(ParsingPollingPlaces.parseLines)
      .get
  }
}


object RawDataStore {
  def apply(location: Path): RawDataStore = new RawDataStore(location)
}