package au.id.tmm.senatedb.rawdata.download

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.rawdata.TestsRawData
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import org.scalatest.BeforeAndAfter

class LoadingFirstPreferencesSpec extends ImprovedFlatSpec with BeforeAndAfter with TestsRawData {

  "the loading of raw first preferences data" should "download the data" in {
    val source = LoadingFirstPreferences.csvLinesOf(testingRawDataDir,
      SenateElection.`2016`)

    val numLines = source.get.getLines().size

    assert(numLines === 839)
  }
}
