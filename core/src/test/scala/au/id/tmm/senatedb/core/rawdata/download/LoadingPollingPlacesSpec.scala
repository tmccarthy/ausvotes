package au.id.tmm.senatedb.core.rawdata.download

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.utilities.testing.{ImprovedFlatSpec, NeedsCleanDirectory}

class LoadingPollingPlacesSpec extends ImprovedFlatSpec with NeedsCleanDirectory {

  "the loading of polling place data" should "not be supported for the 2013 election" in {
    intercept[UnsupportedOperationException] {
      LoadingPollingPlaces.csvLinesOf(cleanDirectory, SenateElection.`2013`).get
    }
  }

}
