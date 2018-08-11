package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.core.engine.ParsedDataStore
import au.id.tmm.ausvotes.core.model.parsing.Ballot
import au.id.tmm.ausvotes.core.model.{CountData, SenateElection}
import au.id.tmm.ausvotes.core.rawdata.RawDataStore
import au.id.tmm.utilities.collection.CloseableIterator
import au.id.tmm.utilities.geo.australia.State

object MockParsedDataStore extends ParsedDataStore {
  override def groupsAndCandidatesFor(election: SenateElection): au.id.tmm.ausvotes.core.model.GroupsAndCandidates =
    GroupAndCandidateFixture.ACT.groupsAndCandidates

  override def divisionsAndPollingPlacesFor(election: SenateElection): au.id.tmm.ausvotes.core.model.DivisionsAndPollingPlaces =
    DivisionAndPollingPlaceFixture.ACT.divisionsAndPollingPlaces

  override def countDataFor(election: SenateElection,
                            allGroupsAndCandidates: au.id.tmm.ausvotes.core.model.GroupsAndCandidates,
                            state: State): CountData = {
    ParsedDataStore(RawDataStore(MockAecResourceStore))
      .countDataFor(election, allGroupsAndCandidates, state)
  }

  override def ballotsFor(election: SenateElection,
                          groupsAndCandidates: au.id.tmm.ausvotes.core.model.GroupsAndCandidates,
                          divisionsAndPollingPlaces: au.id.tmm.ausvotes.core.model.DivisionsAndPollingPlaces,
                          state: State): CloseableIterator[Ballot] = {
    ParsedDataStore(RawDataStore(MockAecResourceStore))
      .ballotsFor(election, groupsAndCandidates, divisionsAndPollingPlaces, state)
  }
}
