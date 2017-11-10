package au.id.tmm.ausvotes.core.rawdata.download

import java.net.URL
import java.nio.file.{Files, Path}

import au.id.tmm.utilities.hashing.Digest
import au.id.tmm.utilities.io.FileUtils.ImprovedPath

object DownloadUtils {
  def downloadUrlToFile(url: URL, target: Path): Unit = {
    for (downloadStream <- resource.managed(url.openStream())) {
      Files.copy(downloadStream, target)
    }
  }

  def throwIfDigestMismatch(file: Path, expectedDigest: Digest): Unit = {
    val actualDigest = file.sha256Checksum.get

    if (actualDigest != expectedDigest) {
      throw new DataIntegrityException(s"Data integrity check failed for file at $file",
        expectedDigest, actualDigest)
    }
  }
}
