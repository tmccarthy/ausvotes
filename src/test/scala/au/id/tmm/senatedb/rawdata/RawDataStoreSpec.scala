package au.id.tmm.senatedb.rawdata

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import resource.managed

class RawDataStoreSpec extends ImprovedFlatSpec {

  private val rawDataStore = RawDataStore(MockAecResourceStore)

  behaviour of "the raw data store"

  it should "retrieve formal preferences for the NT as expected" in {
    for {
      formalPreferencesRows <- managed(rawDataStore.formalPreferencesFor(SenateElection.`2016`, State.NT))
    } {
      assert(formalPreferencesRows.size === 4)
    }
  }

  it should "retrieve first preferences as expected" in {
    for {
      firstPreferencesRows <- managed(rawDataStore.firstPreferencesFor(SenateElection.`2016`))
    } {
      assert(firstPreferencesRows.size === 9)
    }
  }

  it should "retrieve the distribution of preferences for the NT as expected" in {
    for {
      dopRows <- managed(rawDataStore.distributionsOfPreferencesFor(SenateElection.`2016`, State.NT))
    } {
      assert(dopRows.size === 21)
    }
  }

  it should "retrieve the polling places as expected" in {
    for {
      pollingPlacesRows <- managed(rawDataStore.pollingPlacesFor(SenateElection.`2016`))
    } {
      assert(pollingPlacesRows.size === 8328)
    }
  }
}