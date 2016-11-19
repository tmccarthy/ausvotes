package au.id.tmm.senatedb.fixtures

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.parsing.countdata.CountDataGeneration
import au.id.tmm.senatedb.rawdata.RawDataStore
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.resources.ManagedResourceUtils.ExtractableManagedResourceOps

trait TestsCountData {
  private val rawDataStore = RawDataStore(MockAecResourceStore)
  private val election = SenateElection.`2016`
  private val groupsAndCandidates = GroupsAndCandidates.ACT.groupsAndCandidates

  private val state = State.ACT

  private val ballotMaker = Ballots.ACT.ballotMaker

  lazy val countData = resource.managed(rawDataStore.distributionsOfPreferencesFor(election, state))
    .map(CountDataGeneration.fromDistributionOfPreferencesRows(election, state, groupsAndCandidates, _))
    .toTry
    .get

}
