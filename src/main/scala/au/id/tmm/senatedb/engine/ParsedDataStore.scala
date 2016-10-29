package au.id.tmm.senatedb.engine

import au.id.tmm.senatedb.model.flyweights._
import au.id.tmm.senatedb.model.parsing.Ballot
import au.id.tmm.senatedb.model.{DivisionsAndPollingPlaces, GroupsAndCandidates, SenateElection}
import au.id.tmm.senatedb.parsing.{BallotGeneration, DivisionAndPollingPlaceGeneration, GroupAndCandidateGeneration, RawPreferenceParser}
import au.id.tmm.senatedb.rawdata.RawDataStore
import au.id.tmm.utilities.collection.CloseableIterator
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.resources.ManagedResourceUtils.ExtractableManagedResourceOps

trait ParsedDataStore {
  def groupsAndCandidatesFor(election: SenateElection): GroupsAndCandidates

  def divisionsAndPollingPlacesFor(election: SenateElection): DivisionsAndPollingPlaces

  def ballotsFor(election: SenateElection,
                 groupsAndCandidates: GroupsAndCandidates,
                 divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                 state: State): CloseableIterator[Ballot]
}

object ParsedDataStore {
  def apply(rawDataStore: RawDataStore): ParsedDataStore = new ParsedRawDataStore(rawDataStore)
}

private final class ParsedRawDataStore (rawDataStore: RawDataStore) extends ParsedDataStore {
  private val groupFlyweight = GroupFlyweight()
  private val partyFlyweight = RegisteredPartyFlyweight()
  private val postcodeFlyweight = PostcodeFlyweight()
  private val divisionFlyweight = DivisionFlyweight()

  override def groupsAndCandidatesFor(election: SenateElection): GroupsAndCandidates = {
    resource.managed(rawDataStore.firstPreferencesFor(election))
      .map(rows => GroupAndCandidateGeneration.fromFirstPreferencesRows(election, rows, groupFlyweight, partyFlyweight))
      .toTry
      .get
  }

  override def divisionsAndPollingPlacesFor(election: SenateElection): DivisionsAndPollingPlaces = {
    resource.managed(rawDataStore.pollingPlacesFor(election))
      .map(rows => DivisionAndPollingPlaceGeneration.fromPollingPlaceRows(election, rows, divisionFlyweight, postcodeFlyweight))
      .toTry
      .get
  }

  override def ballotsFor(election: SenateElection,
                 groupsAndCandidates: GroupsAndCandidates,
                 divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                 state: State): CloseableIterator[Ballot] = {

    val rawPreferenceParser = RawPreferenceParser(election, state, groupsAndCandidates)

    rawDataStore.formalPreferencesFor(election, state)
      .map(row => {
        BallotGeneration.fromFormalPreferencesRow(
          election,
          state,
          rawPreferenceParser,
          divisionsAndPollingPlaces.lookupDivisionByName,
          (state, name) => divisionsAndPollingPlaces.lookupPollingPlaceByName(state, name),
          row)
      })
  }
}