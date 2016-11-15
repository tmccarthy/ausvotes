package au.id.tmm.senatedb.rawdata.download

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.utilities.testing.{ImprovedFlatSpec, NeedsCleanDirectory}

class LoadingFirstPreferencesSpec extends ImprovedFlatSpec with NeedsCleanDirectory {

  "the loading of first preferences data" should "not be supported for the 2013 election" in {
    intercept[UnsupportedOperationException] {
      LoadingFirstPreferences.csvLinesOf(cleanDirectory, SenateElection.`2013`).get
    }
  }

}
