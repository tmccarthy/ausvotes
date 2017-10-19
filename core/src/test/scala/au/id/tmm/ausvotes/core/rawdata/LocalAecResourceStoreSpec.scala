package au.id.tmm.ausvotes.core.rawdata

import java.nio.file.{Files, NotDirectoryException, Paths}

import au.id.tmm.utilities.testing.{ImprovedFlatSpec, NeedsCleanDirectory}

class LocalAecResourceStoreSpec extends ImprovedFlatSpec with NeedsCleanDirectory {

  "an AEC resource store" should "fail if the provided file is not a directory" in {
    val file = cleanDirectory.resolve("testFile.txt")

    Files.createFile(file)

    intercept[NotDirectoryException] {
      AecResourceStore.at(file)
    }
  }

  it should "create the directory if one is missing" in {
    val directory = cleanDirectory.resolve("storeDirectory")

    AecResourceStore.at(directory)

    assert(Files.isDirectory(directory))
  }

  it should "retain the directory as an absolute path" in {
    val workingDirectory = Paths.get(".").toAbsolutePath
    val relativePath = workingDirectory.relativize(cleanDirectory.resolve("storeDirectory"))

    val sut = AecResourceStore.at(relativePath)

    assert(sut.asInstanceOf[LocalAecResourceStore].location.isAbsolute)
  }
}
