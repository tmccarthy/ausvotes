package au.id.tmm.senatedb.fixtures

import au.id.tmm.senatedb.engine.ParsedDataStore
import au.id.tmm.senatedb.model
import au.id.tmm.senatedb.model.parsing.Ballot
import au.id.tmm.senatedb.model.{CountData, SenateElection}
import au.id.tmm.senatedb.rawdata.RawDataStore
import au.id.tmm.utilities.collection.CloseableIterator
import au.id.tmm.utilities.geo.australia.State

object MockParsedDataStore extends ParsedDataStore {
  override def groupsAndCandidatesFor(election: SenateElection): model.GroupsAndCandidates =
    GroupsAndCandidates.ACT.groupsAndCandidates

  override def divisionsAndPollingPlacesFor(election: SenateElection): model.DivisionsAndPollingPlaces =
    DivisionsAndPollingPlaces.ACT.divisionsAndPollingPlaces

  override def countDataFor(election: SenateElection,
                            allGroupsAndCandidates: model.GroupsAndCandidates,
                            state: State): CountData = {
    ParsedDataStore(RawDataStore(MockAecResourceStore))
      .countDataFor(election, allGroupsAndCandidates, state)
  }

  override def ballotsFor(election: SenateElection,
                          groupsAndCandidates: model.GroupsAndCandidates,
                          divisionsAndPollingPlaces: model.DivisionsAndPollingPlaces,
                          state: State): CloseableIterator[Ballot] = {
    ParsedDataStore(RawDataStore(MockAecResourceStore))
      .ballotsFor(election, groupsAndCandidates, divisionsAndPollingPlaces, state)
  }
}
