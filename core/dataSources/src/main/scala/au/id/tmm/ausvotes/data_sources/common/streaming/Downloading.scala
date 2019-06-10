package au.id.tmm.ausvotes.data_sources.common.streaming

import java.io.{IOException, InputStream}
import java.nio.file.{Files, Path}

import au.id.tmm.bfect.effects.Sync

object Downloading {

  def downloadToPath[F[+_, +_] : Sync](
                                        makeDownloadedInputStream: F[IOException, InputStream],
                                        destination: Path,
                                      ): F[IOException, Unit] =
    Sync.bracketCloseable[F, InputStream, IOException, Unit] {
      makeDownloadedInputStream
    } { stream =>
      syncCatchIOException(Files.copy(stream, destination))
    }

}
