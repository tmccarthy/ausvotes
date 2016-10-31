package au.id.tmm.senatedb.fixtures

import au.id.tmm.senatedb.engine.ParsedDataStore
import au.id.tmm.senatedb.model.parsing.Ballot
import au.id.tmm.senatedb.model.{DivisionsAndPollingPlaces, GroupsAndCandidates, SenateElection}
import au.id.tmm.senatedb.rawdata.RawDataStore
import au.id.tmm.utilities.collection.CloseableIterator
import au.id.tmm.utilities.geo.australia.State

object MockParsedDataStore extends ParsedDataStore {
  override def groupsAndCandidatesFor(election: SenateElection): GroupsAndCandidates =
    GroupsAndCandidates.ACT.groupsAndCandidates

  override def divisionsAndPollingPlacesFor(election: SenateElection): DivisionsAndPollingPlaces =
    DivisionsAndPollingPlaces.ACT.divisionsAndPollingPlaces

  override def ballotsFor(election: SenateElection,
                          groupsAndCandidates: GroupsAndCandidates,
                          divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                          state: State): CloseableIterator[Ballot] = {
    ParsedDataStore(RawDataStore(MockAecResourceStore))
      .ballotsFor(election, groupsAndCandidates, divisionsAndPollingPlaces, state)
  }
}
