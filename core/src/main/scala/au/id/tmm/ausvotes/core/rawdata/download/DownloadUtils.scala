package au.id.tmm.ausvotes.core.rawdata.download

import java.net.URL
import java.nio.file.{Files, Path}

import au.id.tmm.utilities.hashing.Digest
import au.id.tmm.utilities.io.FileUtils.ImprovedPath

import scala.util.{Failure, Success, Try}

object DownloadUtils {
  def downloadUrlToFile(url: URL, target: Path): Try[Unit] = Try {
    for (downloadStream <- resource.managed(url.openStream())) {
      Files.copy(downloadStream, target)
    }
  }

  def localResourceIntegrityCheck(file: Path, expectedDigest: Digest): Try[Unit] = {
    file.sha256Checksum
      .flatMap(actualDigest => {
        val digestsMatch = actualDigest == expectedDigest

        if (digestsMatch) {
          Success(Unit)
        } else {
          Failure(
            new DataIntegrityException(s"Data integrity check failed for file at $file",
              expectedDigest, actualDigest)
          )
        }
      })
  }
}
