package au.id.tmm.senatedb.data.rawdatastore.download

import au.id.tmm.senatedb.model.{SenateElection, State}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class LoadingDistributionsOfPreferencesSpec extends ImprovedFlatSpec with TestsRawData {

  "the loading of distribution of preferences data" should "fail if the data hasn't been downloaded and we disallow downloading" in {
    val source = LoadingDistributionsOfPreferences.csvLinesOf(testingRawDataDir,
      SenateElection.`2016`, State.NT,
      shouldDownloadIfNeeded = false)

    assert(source.failed.get.isInstanceOf[DataMissingDownloadDisallowedException])
  }

  it should "be successful" in {
    val source = LoadingDistributionsOfPreferences.csvLinesOf(testingRawDataDir,
      SenateElection.`2016`, State.NT)

    val numLines = source.get.size

    assert(numLines === 3579)
  }
}
