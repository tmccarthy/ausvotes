package au.id.tmm.senatedb.rawdata.download

import java.net.URL
import java.nio.charset.Charset
import java.nio.file.{Files, Path, Paths}

import au.id.tmm.senatedb.rawdata.resources.{Resource, ResourceWithDigest}
import au.id.tmm.utilities.hashing.Digest
import au.id.tmm.utilities.testing.{ImprovedFlatSpec, NeedsCleanDirectory}

import scala.util.Success

class StorageUtilsSpec extends ImprovedFlatSpec with NeedsCleanDirectory {

  private val resourceUrl = getClass.getResource("test_resource.txt")
  private val expectedContent = "Hello World!"

  private val testResource = new Resource {
    override def localFileName: Path = Paths.get("test.txt").getFileName

    override def url: URL = resourceUrl
  }

  private val testResourceWithDigest = new ResourceWithDigest {
    override def digest: Digest = Digest("7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069")

    override def localFileName: Path = Paths.get("test.txt").getFileName

    override def url: URL = resourceUrl
  }

  "the storage utils" should "download the resource to the location if it is missing" in {
    val expectedLocation = StorageUtils.findRawDataFor(cleanDirectory, testResource)

    val actualContent = expectedLocation.map(Files.readAllBytes).map(new String(_))

    assert(actualContent === Success(expectedContent))
  }

  it should "not download the resource if the file is already present" in {
    Given("the file already exists")

    val existingContent = "test"
    writeContentToExpectedLocation(existingContent)

    When("we find the file")

    val expectedLocation = StorageUtils.findRawDataFor(cleanDirectory, testResource)

    Then("the original file remains")

    val actualContent = expectedLocation.map(Files.readAllBytes).map(new String(_))

    assert(actualContent === Success(existingContent))
  }

  it should "download a resource with a digest if it is missing" in {
    val expectedLocation = StorageUtils.findRawDataWithIntegrityCheckFor(cleanDirectory, testResourceWithDigest)

    val actualContent = expectedLocation.map(Files.readAllBytes).map(new String(_))

    assert(actualContent === Success(expectedContent))
  }

  it should "not download the resource if a matching file is already present" in {
    Given("the file already exists with the correct content")

    val existingContent = "Hello World!"
    writeContentToExpectedLocation(existingContent)

    When("we find the file")

    val expectedLocation = StorageUtils.findRawDataWithIntegrityCheckFor(cleanDirectory, testResourceWithDigest)

    Then("the original file remains")

    val actualContent = expectedLocation.map(Files.readAllBytes).map(new String(_))

    assert(actualContent === Success(existingContent))
  }

  it should "fail if the file has already been downloaded with different content" in {
    Given("the file already exists with incorrect content")

    val existingContent = "test"
    writeContentToExpectedLocation(existingContent)

    When("we find the file")

    val expectedLocation = StorageUtils.findRawDataWithIntegrityCheckFor(cleanDirectory, testResourceWithDigest)

    Then("the find operation fails")

    assert(expectedLocation.isFailure)
    assert(expectedLocation.failed.get.isInstanceOf[DataIntegrityException])
  }

  private def writeContentToExpectedLocation(content: String): Unit = {
    val existingLocation = cleanDirectory.resolve("test.txt")

    Files.write(existingLocation, content.getBytes(Charset.forName("UTF-8")))
  }
}
