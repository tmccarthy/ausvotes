package au.id.tmm.senatedb.data.rawdatastore.download

import java.nio.file.{Files, Paths}

import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfterEach, Suite}

private [data] trait TestingRawData extends BeforeAndAfterEach { this: Suite =>
  val testingRawDataDir = Paths.get("testingRawData")

  override def beforeEach(): Unit = {
    FileUtils.deleteDirectory(testingRawDataDir.toFile)
    Files.createDirectory(testingRawDataDir)
    super.beforeEach()
  }
}
