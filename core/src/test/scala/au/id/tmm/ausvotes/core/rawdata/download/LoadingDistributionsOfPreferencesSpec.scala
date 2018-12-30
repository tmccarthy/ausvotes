package au.id.tmm.ausvotes.core.rawdata.download

import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import au.id.tmm.utilities.testing.{ImprovedFlatSpec, NeedsCleanDirectory}

class LoadingDistributionsOfPreferencesSpec extends ImprovedFlatSpec with NeedsCleanDirectory {

  "the loading of DOP data" should "not be supported for the 2013 election" in {
    intercept[UnsupportedOperationException] {
      LoadingDistributionsOfPreferences.resourceMatching(SenateElection.`2013`).get
    }
  }

}
