package au.id.tmm.senatedb.rawdata

import java.nio.file.Files

import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfterEach, OneInstancePerTest, Suite}

trait TestsRawData extends BeforeAndAfterEach { this: Suite with OneInstancePerTest =>
  val testingRawDataDir = Files.createTempDirectory("testingRawData")

  lazy val rawDataStore = RawDataStore(testingRawDataDir)

  override def beforeEach(): Unit = {
    FileUtils.deleteDirectory(testingRawDataDir.toFile)
    Files.createDirectory(testingRawDataDir)
    super.beforeEach()
  }
}
