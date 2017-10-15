package au.id.tmm.ausvotes.core.rawdata

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.rawdata.csv.{ParsingDistributionOfPreferences, ParsingFirstPreferences, ParsingFormalPreferences, ParsingPollingPlaces}
import au.id.tmm.ausvotes.core.rawdata.model.{DistributionOfPreferencesRow, FirstPreferencesRow, FormalPreferencesRow, PollingPlacesRow}
import au.id.tmm.utilities.collection.CloseableIterator
import au.id.tmm.utilities.geo.australia.State

final class RawDataStore private (aecResourceStore: AecResourceStore) {
  def distributionsOfPreferencesFor(election: SenateElection, state: State): CloseableIterator[DistributionOfPreferencesRow] = {
    aecResourceStore.distributionOfPreferencesFor(election, state)
      .flatMap(ParsingDistributionOfPreferences.parseLines)
      .get
  }

  def firstPreferencesFor(election: SenateElection): CloseableIterator[FirstPreferencesRow] = {
    aecResourceStore.firstPreferencesFor(election)
      .flatMap(ParsingFirstPreferences.parseLines)
      .get
  }

  def formalPreferencesFor(election: SenateElection, state: State): CloseableIterator[FormalPreferencesRow] = {
    aecResourceStore.formalPreferencesFor(election, state)
      .flatMap(ParsingFormalPreferences.parseLines)
      .get
  }

  def pollingPlacesFor(election: SenateElection): CloseableIterator[PollingPlacesRow] = {
    aecResourceStore.pollingPlacesFor(election)
      .flatMap(ParsingPollingPlaces.parseLines)
      .get
  }
}

object RawDataStore {
  def apply(aecResourceStore: AecResourceStore): RawDataStore = new RawDataStore(aecResourceStore)
}