package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.core.engine.ParsedDataStore
import au.id.tmm.ausvotes.core.rawdata.RawDataStore
import au.id.tmm.ausvotes.model.federal.FederalElection
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.utilities.collection.CloseableIterator

// TODO move to the core tests
object MockParsedDataStore extends ParsedDataStore {
  override def groupsAndCandidatesFor(election: SenateElection): au.id.tmm.ausvotes.core.model.GroupsAndCandidates =
    GroupAndCandidateFixture.ACT.groupsAndCandidates

  override def divisionsAndPollingPlacesFor(election: FederalElection): au.id.tmm.ausvotes.core.model.DivisionsAndPollingPlaces =
    DivisionAndPollingPlaceFixture.ACT.divisionsAndPollingPlaces

  override def countDataFor(
                             election: SenateElectionForState,
                             allGroupsAndCandidates: au.id.tmm.ausvotes.core.model.GroupsAndCandidates,
                           ): SenateCountData = {
    ParsedDataStore(RawDataStore(MockAecResourceStore))
      .countDataFor(election, allGroupsAndCandidates)
  }

  override def ballotsFor(
                           election: SenateElectionForState,
                           groupsAndCandidates: au.id.tmm.ausvotes.core.model.GroupsAndCandidates,
                           divisionsAndPollingPlaces: au.id.tmm.ausvotes.core.model.DivisionsAndPollingPlaces,
                         ): CloseableIterator[SenateBallot] = {
    ParsedDataStore(RawDataStore(MockAecResourceStore))
      .ballotsFor(election, groupsAndCandidates, divisionsAndPollingPlaces)
  }
}
