package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.core.engine.ParsedDataStore
import au.id.tmm.ausvotes.core.rawdata.RawDataStore
import au.id.tmm.ausvotes.model.federal.{DivisionsAndPollingPlaces, FederalElection}
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.utilities.collection.CloseableIterator

// TODO move to the core tests
object MockParsedDataStore extends ParsedDataStore {
  override def groupsAndCandidatesFor(election: SenateElection): SenateGroupsAndCandidates =
    GroupAndCandidateFixture.ACT.groupsAndCandidates

  override def divisionsAndPollingPlacesFor(election: FederalElection): DivisionsAndPollingPlaces =
    DivisionAndPollingPlaceFixture.ACT.divisionsAndPollingPlaces

  override def countDataFor(
                             election: SenateElectionForState,
                             allGroupsAndCandidates: SenateGroupsAndCandidates,
                           ): SenateCountData = {
    ParsedDataStore(RawDataStore(MockAecResourceStore))
      .countDataFor(election, allGroupsAndCandidates)
  }

  override def ballotsFor(
                           election: SenateElectionForState,
                           groupsAndCandidates: SenateGroupsAndCandidates,
                           divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                         ): CloseableIterator[SenateBallot] = {
    ParsedDataStore(RawDataStore(MockAecResourceStore))
      .ballotsFor(election, groupsAndCandidates, divisionsAndPollingPlaces)
  }
}
