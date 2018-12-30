package au.id.tmm.ausvotes.core.rawdata.download

import au.id.tmm.ausvotes.model.federal.FederalElection
import au.id.tmm.utilities.testing.{ImprovedFlatSpec, NeedsCleanDirectory}

class LoadingPollingPlacesSpec extends ImprovedFlatSpec with NeedsCleanDirectory {

  "the loading of polling place data" should "not be supported for the 2013 election" in {
    intercept[UnsupportedOperationException] {
      LoadingPollingPlaces.resourceMatching(FederalElection.`2013`).get
    }
  }

}
