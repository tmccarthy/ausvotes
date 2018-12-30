package au.id.tmm.ausvotes.core.rawdata.download

import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import au.id.tmm.utilities.testing.{ImprovedFlatSpec, NeedsCleanDirectory}

class LoadingFirstPreferencesSpec extends ImprovedFlatSpec with NeedsCleanDirectory {

  "the loading of first preferences data" should "not be supported for the 2013 election" in {
    intercept[UnsupportedOperationException] {
      LoadingFirstPreferences.resourceMatching(SenateElection.`2013`).get
    }
  }

}
