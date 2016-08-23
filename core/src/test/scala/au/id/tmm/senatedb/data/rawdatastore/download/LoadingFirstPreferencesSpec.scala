package au.id.tmm.senatedb.data.rawdatastore.download

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import org.scalatest.BeforeAndAfter

class LoadingFirstPreferencesSpec extends ImprovedFlatSpec with BeforeAndAfter with TestingRawData {
  behaviour of "the loading of raw first preferences data"

  it should "fail if the data hasn't been downloaded, and we disallow downloading" in {
    val source = LoadingFirstPreferences.csvLinesOf(testingRawDataDir,
      SenateElection.`2016`,
      shouldDownloadIfNeeded = false)

    assert(source.failed.get.isInstanceOf[IllegalStateException])
  }

  it should "download the data" in {
    val source = LoadingFirstPreferences.csvLinesOf(testingRawDataDir,
      SenateElection.`2016`)

    val numLines = source.get.size

    assert(numLines === 62547)
  }

}
