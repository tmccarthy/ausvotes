package au.id.tmm.senatedb.rawdata

import java.nio.file.Files

import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfterEach, OneInstancePerTest, Suite}

trait NeedsCleanDirectory extends BeforeAndAfterEach { this: Suite with OneInstancePerTest =>
  val cleanDirectory = Files.createTempDirectory("cleanDir")

  override def beforeEach(): Unit = {
    FileUtils.deleteDirectory(cleanDirectory.toFile)
    Files.createDirectory(cleanDirectory)
    super.beforeEach()
  }
}
