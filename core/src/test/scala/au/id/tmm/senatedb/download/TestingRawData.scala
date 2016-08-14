package au.id.tmm.senatedb.download

import java.nio.file.{Files, Paths}

import org.apache.commons.io.FileUtils
import org.scalatest.BeforeAndAfter

trait TestingRawData { this: BeforeAndAfter =>
  val testingRawDataDir = Paths.get("testingRawData")

  before {
    FileUtils.deleteDirectory(testingRawDataDir.toFile)
    Files.createDirectory(testingRawDataDir)
  }
}
