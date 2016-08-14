package au.id.tmm.senatedb.download

import java.net.URL
import java.nio.file.{Files, Path}

import scala.util.Try

object downloadUrlToFile extends ((URL, Path) => Try[Unit]) {
  override def apply(url: URL, target: Path): Try[Unit] = Try {
    for (downloadStream <- resource.managed(url.openStream())) {
      Files.copy(downloadStream, target)
    }
  }
}
