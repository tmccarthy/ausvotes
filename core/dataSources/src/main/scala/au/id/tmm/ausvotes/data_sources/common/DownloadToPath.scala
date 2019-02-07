package au.id.tmm.ausvotes.data_sources.common

import java.io.IOException
import java.net.URL
import java.nio.file.{Files, Path}

import scalaz.zio.IO

trait DownloadToPath[F[+_, +_]] {

  def downloadToPath(url: URL, target: Path): F[IOException, Unit]

}

object DownloadToPath {

  def downloadToPath[F[+_, +_] : DownloadToPath](url: URL, target: Path): F[IOException, Unit] =
    implicitly[DownloadToPath[F]].downloadToPath(url, target)

  object Always extends DownloadToPath[IO] {
    override def downloadToPath(url: URL, target: Path): IO[IOException, Unit] =
      IO.bracket {
        IO.syncCatch(url.openStream()) {
          case e: IOException => e
        }
      } { stream =>
        IO.sync(stream.close())
      } { stream =>
        IO.syncCatch(Files.copy(stream, target)) {
          case e: IOException => e
        }.map(_ => Unit)
      }
  }

  object IfTargetMissing extends DownloadToPath[IO] {
    override def downloadToPath(url: URL, target: Path): IO[IOException, Unit] =
      for {
        fileAlreadyThere <- IO.syncCatch(Files.exists(target)) {
          case e: IOException => e
        }
        done <- if (fileAlreadyThere) IO.unit else DownloadToPath.Always.downloadToPath(url, target)
      } yield done

  }

}
