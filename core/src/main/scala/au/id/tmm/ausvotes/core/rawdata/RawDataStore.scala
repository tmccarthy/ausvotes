package au.id.tmm.ausvotes.core.rawdata

import au.id.tmm.ausvotes.core.rawdata.csv.{ParsingDistributionOfPreferences, ParsingFirstPreferences, ParsingFormalPreferences, ParsingPollingPlaces}
import au.id.tmm.ausvotes.core.rawdata.model.{DistributionOfPreferencesRow, FirstPreferencesRow, FormalPreferencesRow, PollingPlacesRow}
import au.id.tmm.ausvotes.model.federal.FederalElection
import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, SenateElectionForState}
import au.id.tmm.utilities.collection.CloseableIterator

final class RawDataStore private (aecResourceStore: AecResourceStore) {
  def distributionsOfPreferencesFor(election: SenateElectionForState): CloseableIterator[DistributionOfPreferencesRow] = {
    aecResourceStore.distributionOfPreferencesFor(election)
      .flatMap(ParsingDistributionOfPreferences.parseLines)
      .get
  }

  def firstPreferencesFor(election: SenateElection): CloseableIterator[FirstPreferencesRow] = {
    aecResourceStore.firstPreferencesFor(election)
      .flatMap(ParsingFirstPreferences.parseLines)
      .get
  }

  def formalPreferencesFor(election: SenateElectionForState): CloseableIterator[FormalPreferencesRow] = {
    aecResourceStore.formalPreferencesFor(election)
      .flatMap(ParsingFormalPreferences.parseLines)
      .get
  }

  def pollingPlacesFor(election: FederalElection): CloseableIterator[PollingPlacesRow] = {
    aecResourceStore.pollingPlacesFor(election)
      .flatMap(ParsingPollingPlaces.parseLines)
      .get
  }
}

object RawDataStore {
  def apply(aecResourceStore: AecResourceStore): RawDataStore = new RawDataStore(aecResourceStore)
}
