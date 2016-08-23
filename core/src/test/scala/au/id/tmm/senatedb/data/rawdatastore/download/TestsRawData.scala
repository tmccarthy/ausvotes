package au.id.tmm.senatedb.data.rawdatastore.download

import java.nio.file.{Files, Paths}

import au.id.tmm.senatedb.data.rawdatastore.RawDataStore
import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfterEach, Suite}

private [data] trait TestsRawData extends BeforeAndAfterEach { this: Suite =>
  val testingRawDataDir = Paths.get("testingRawData")

  lazy val rawDataStore = RawDataStore(testingRawDataDir)

  override def beforeEach(): Unit = {
    FileUtils.deleteDirectory(testingRawDataDir.toFile)
    Files.createDirectory(testingRawDataDir)
    super.beforeEach()
  }
}
