package au.id.tmm.senatedb.data.download

import java.nio.file.Path

import au.id.tmm.utilities.files.FileUtils.ImprovedPath
import au.id.tmm.utilities.hashing.Digest

import scala.util.{Failure, Success, Try}

private[download] object localResourceIntegrityCheck extends ((Path, Digest) => Try[Unit]) {

  def apply(file: Path, expectedDigest: Digest): Try[Unit] = {
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
