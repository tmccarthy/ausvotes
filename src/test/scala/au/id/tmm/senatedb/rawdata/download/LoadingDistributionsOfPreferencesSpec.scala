package au.id.tmm.senatedb.rawdata.download

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.{ImprovedFlatSpec, NeedsCleanDirectory}

class LoadingDistributionsOfPreferencesSpec extends ImprovedFlatSpec with NeedsCleanDirectory {

  "the loading of DOP data" should "not be supported for the 2013 election" in {
    intercept[UnsupportedOperationException] {
      LoadingDistributionsOfPreferences.csvLinesOf(cleanDirectory, SenateElection.`2013`, State.ACT).get
    }
  }

}
