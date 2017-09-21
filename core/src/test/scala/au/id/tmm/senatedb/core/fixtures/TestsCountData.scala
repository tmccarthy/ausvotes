package au.id.tmm.senatedb.core.fixtures

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.parsing.countdata.CountDataGeneration
import au.id.tmm.senatedb.core.rawdata.RawDataStore
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.resources.ManagedResourceUtils.ExtractableManagedResourceOps

trait TestsCountData {
  private val rawDataStore = RawDataStore(MockAecResourceStore)
  private val election = SenateElection.`2016`
  private val groupsAndCandidates = GroupAndCandidateFixture.ACT.groupsAndCandidates

  private val state = State.ACT

  private val ballotMaker = BallotFixture.ACT.ballotMaker

  lazy val countData = resource.managed(rawDataStore.distributionsOfPreferencesFor(election, state))
    .map(CountDataGeneration.fromDistributionOfPreferencesRows(election, state, groupsAndCandidates, _))
    .toTry
    .get

}
