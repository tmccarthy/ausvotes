package au.id.tmm.senatedb.rawdata.download

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.rawdata.TestsRawData
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class LoadingDistributionsOfPreferencesSpec extends ImprovedFlatSpec with TestsRawData {

  "the loading of distribution of preferences data" should "be successful" in {
    val source = LoadingDistributionsOfPreferences.csvLinesOf(testingRawDataDir,
      SenateElection.`2016`, State.NT)

    val numLines = source.get.size

    assert(numLines === 3579)
  }
}
