package au.id.tmm.ausvotes.core.rawdata.download

import java.nio.file.{Files, Path}

import au.id.tmm.utilities.logging.LoggedEvent.TryOps
import au.id.tmm.utilities.logging.Logger
import au.id.tmm.ausvotes.core.rawdata.resources.{Resource, ResourceWithDigest}

import scala.annotation.tailrec
import scala.util.Try

object StorageUtils {

  private implicit val logger = Logger()

  def findRawDataFor(resource: Resource, dataDir: Path, performDigestCheck: Boolean = false): Try[Path] = {
    Try {
      findRawDataImpl(resource, dataDir, performDigestCheck)
    }.logEvent(
      "FIND_RAW_DATA_FOR_RESOURCE",
      "resource" -> resource.url,
    )
  }

  @tailrec
  private def findRawDataImpl(resource: Resource, dataDir: Path, performDigestCheck: Boolean): Path = {
    val expectedPath = dataDir.resolve(resource.localFileName)

    if (!Files.exists(expectedPath)) {
      DownloadUtils.downloadUrlToFile(resource.url, expectedPath)
      findRawDataImpl(resource, dataDir, performDigestCheck)

    } else {
      resource match {
        case r: ResourceWithDigest => {
          if (performDigestCheck) {
            DownloadUtils.throwIfDigestMismatch(expectedPath, r.digest)
          }
          expectedPath
        }
        case _: Resource => {
          expectedPath
        }
      }
    }
  }
}
