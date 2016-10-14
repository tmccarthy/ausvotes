package au.id.tmm.senatedb.rawdata.download

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.rawdata.TestsRawData
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class LoadingPollingPlacesSpec extends ImprovedFlatSpec with TestsRawData {
  "the loading of raw polling places data" should "download the data" in {
    val source = LoadingPollingPlaces.csvLinesOf(testingRawDataDir,
      SenateElection.`2016`)

    val numLines = source.get.getLines().size

    assert(numLines === 8330)
  }
}
