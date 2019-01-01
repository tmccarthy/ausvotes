package au.id.tmm.ausvotes.core.rawdata.download

import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.{ImprovedFlatSpec, NeedsCleanDirectory}

class LoadingFormalPreferencesSpec extends ImprovedFlatSpec with NeedsCleanDirectory {

  "the loading of formal preferences" should "not be supported for the 2013 election" in {
    intercept[UnsupportedOperationException] {
      LoadingFormalPreferences.resourceMatching(SenateElection.`2013`.electionForState(State.ACT).get).get
    }
  }

}
