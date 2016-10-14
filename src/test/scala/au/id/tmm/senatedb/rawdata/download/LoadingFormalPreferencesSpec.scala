package au.id.tmm.senatedb.rawdata.download

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.rawdata.TestsRawData
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import org.scalatest.BeforeAndAfter

class LoadingFormalPreferencesSpec extends ImprovedFlatSpec with BeforeAndAfter with TestsRawData {

  "the loading of raw formal preferences data" should "download the data" in {
    val source = LoadingFormalPreferences.csvLinesOf(testingRawDataDir,
      SenateElection.`2016`,
      State.NT)

    val numLines = source.get.size

    assert(numLines === 6886936)
  }
}
