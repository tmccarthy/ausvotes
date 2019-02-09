package au.id.tmm.ausvotes.core.engine

import au.id.tmm.ausvotes.core.parsing._
import au.id.tmm.ausvotes.core.parsing.countdata.CountDataGeneration
import au.id.tmm.ausvotes.core.rawdata.RawDataStore
import au.id.tmm.ausvotes.model.Flyweights.{ElectorateFlyweight, GroupFlyweight}
import au.id.tmm.ausvotes.model.federal.{DivisionsAndPollingPlaces, FederalElection}
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.utilities.collection.CloseableIterator
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.logging.LoggedEvent.TryOps
import au.id.tmm.utilities.logging.Logger
import au.id.tmm.utilities.resources.ManagedResourceUtils.ExtractableManagedResourceOps

trait ParsedDataStore {
  def groupsAndCandidatesFor(election: SenateElection): SenateGroupsAndCandidates

  def divisionsAndPollingPlacesFor(election: FederalElection): DivisionsAndPollingPlaces

  def countDataFor(
                    election: SenateElectionForState,
                    allGroupsAndCandidates: SenateGroupsAndCandidates,
                  ): SenateCountData

  def ballotsFor(
                  election: SenateElectionForState,
                  groupsAndCandidates: SenateGroupsAndCandidates,
                  divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                ): CloseableIterator[SenateBallot]
}

object ParsedDataStore {
  def apply(rawDataStore: RawDataStore): ParsedDataStore = new ParsedRawDataStore(rawDataStore)
}

private final class ParsedRawDataStore (rawDataStore: RawDataStore) extends ParsedDataStore {
  private implicit val logger: Logger = Logger()

  private val groupFlyweight = GroupFlyweight[SenateElectionForState]()
  private val electorateFlyweight = ElectorateFlyweight[FederalElection, State]()

  override def groupsAndCandidatesFor(election: SenateElection): SenateGroupsAndCandidates = {
    resource.managed(rawDataStore.firstPreferencesFor(election))
      .map(rows => GroupAndCandidateGeneration.fromFirstPreferencesRows(election, rows, groupFlyweight))
      .toTry
      .logEvent("PARSE_GROUPS_AND_CANDIDATES", "election" -> election)
      .get
  }

  override def divisionsAndPollingPlacesFor(election: FederalElection): DivisionsAndPollingPlaces = {
    resource.managed(rawDataStore.pollingPlacesFor(election))
      .map(rows => DivisionAndPollingPlaceGeneration.fromPollingPlaceRows(election, rows, electorateFlyweight))
      .toTry
      .logEvent("PARSE_DIVISIONS_AND_POLLING_PLACES", "election" -> election)
      .get
  }

  override def countDataFor(
                             election: SenateElectionForState,
                             allGroupsAndCandidates: SenateGroupsAndCandidates,
                           ): SenateCountData = {
    resource.managed(rawDataStore.distributionsOfPreferencesFor(election))
      .map(rows => CountDataGeneration.fromDistributionOfPreferencesRows(election, allGroupsAndCandidates, rows))
      .toTry
      .logEvent("PARSE_COUNT_DATA", "election" -> election.election, "state" -> election.state.abbreviation)
      .get
  }

  override def ballotsFor(
                           election: SenateElectionForState,
                           groupsAndCandidates: SenateGroupsAndCandidates,
                           divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                         ): CloseableIterator[SenateBallot] = {

    val rawPreferenceParser = RawPreferenceParser(election, groupsAndCandidates)

    rawDataStore.formalPreferencesFor(election)
      .map(row => {
        BallotGeneration.fromFormalPreferencesRow(
          election,
          rawPreferenceParser,
          divisionsAndPollingPlaces.lookupDivisionByName,
          (state, name) => divisionsAndPollingPlaces.lookupPollingPlaceByName(state, name),
          row)
      })
  }
}
