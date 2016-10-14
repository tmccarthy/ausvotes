package au.id.tmm.senatedb.rawdata.download

import java.nio.file.{Files, Path}

import au.id.tmm.senatedb.rawdata.download.DownloadUtils.{downloadUrlToFile, localResourceIntegrityCheck}
import au.id.tmm.senatedb.rawdata.resources.{Resource, ResourceWithDigest}

import scala.util.Try

object StorageUtils {
  def findRawDataFor(dataDir: Path, resource: Resource): Try[Path] = {
    val expectedPath = dataDir.resolve(resource.localFileName)

    if (Files.exists(expectedPath)) {
      Try(expectedPath)

    } else {
      downloadUrlToFile(resource.url, expectedPath)
        .map(_ => expectedPath)

    }
  }

  def findRawDataWithIntegrityCheckFor(dataDir: Path, resource: ResourceWithDigest): Try[Path] = {
    val expectedPath = dataDir.resolve(resource.localFileName)

    if (Files.exists(expectedPath)) {
      localResourceIntegrityCheck(expectedPath, resource.digest).map(_ => expectedPath)

    } else {
      downloadUrlToFile(resource.url, expectedPath)
        .flatMap(_ => localResourceIntegrityCheck(expectedPath, resource.digest))
        .map(_ => expectedPath)

    }
  }
}
