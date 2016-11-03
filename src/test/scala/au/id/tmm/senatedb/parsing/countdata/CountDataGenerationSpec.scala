package au.id.tmm.senatedb.parsing.countdata

import au.id.tmm.senatedb.fixtures.{GroupsAndCandidates, MockAecResourceStore}
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.rawdata.RawDataStore
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountDataGenerationSpec extends ImprovedFlatSpec {

  private val rawDataStore = RawDataStore(MockAecResourceStore)
  private val election = SenateElection.`2016`
  private val groupsAndCandidates = GroupsAndCandidates.ACT.groupsAndCandidates

  "the count data generation" should "work for the ACT" in {
    val state = State.ACT
    for {
      dopRows <- resource.managed(rawDataStore.distributionsOfPreferencesFor(election, state))
    } {
      val countData = CountDataGeneration.fromDistributionOfPreferencesRows(election, state, groupsAndCandidates, dopRows)

      assert(countData === 42)
    }
  }

}
