package au.id.tmm.ausvotes.data_sources.common

import java.io.FileNotFoundException
import java.nio.file.{Files, Path}

import au.id.tmm.utilities.testing.{ImprovedFlatSpec, NeedsCleanDirectory}
import scalaz.zio.RTS

// TODO need a general testing RTS I think
class DownloadToPathSpec extends ImprovedFlatSpec with NeedsCleanDirectory with RTS {

  "downloading a file to a path" should "succeed if the resource is present" in {
    val url = getClass.getResource("/au/id/tmm/ausvotes/data_sources/common/test_resource.txt")
    val target: Path = cleanDirectory.resolve("test_resource.txt")

    val result = unsafeRun(DownloadToPath.Always.downloadToPath(url, target).attempt)

    assert(result === Right(()))
    assert(Files.readAllLines(target).get(0) === "Hello World!")
  }

  it should "fail if the resource is missing" in {
    val url = cleanDirectory.resolve("missing_resource.txt").toUri.toURL
    val target: Path = cleanDirectory.resolve("test_resource.txt")

    val result = unsafeRun(DownloadToPath.Always.downloadToPath(url, target).attempt)

    assert(result.left.map(_.getClass) === Left(classOf[FileNotFoundException]))
  }

}
