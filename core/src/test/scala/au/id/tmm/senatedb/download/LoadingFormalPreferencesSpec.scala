package au.id.tmm.senatedb.download

import java.nio.file.{Files, Paths}

import au.id.tmm.senatedb.model.{SenateElection, State}
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import org.apache.commons.io.FileUtils
import org.scalatest.BeforeAndAfter

class LoadingFormalPreferencesSpec extends ImprovedFlatSpec with BeforeAndAfter {

  val testingRawDataDir = Paths.get("testingRawData")

  before {
    FileUtils.deleteDirectory(testingRawDataDir.toFile)
    Files.createDirectory(testingRawDataDir)
  }

  behaviour of "the loading of raw formal preferences data"

  it should "fail if the data hasn't been downloaded, and we disallow downloading" in {
    val source = LoadingFormalPreferences.csvLinesOf(testingRawDataDir,
      SenateElection.`2016`,
      State.NT,
      shouldDownloadIfNeeded = false)

    assert(source.failed.get.isInstanceOf[IllegalStateException])
  }

  it should "download the data" in {
    val source = LoadingFormalPreferences.csvLinesOf(testingRawDataDir,
      SenateElection.`2016`,
      State.NT)

    val numLines = source.get.size

    assert(numLines === 6886936)
  }

}
