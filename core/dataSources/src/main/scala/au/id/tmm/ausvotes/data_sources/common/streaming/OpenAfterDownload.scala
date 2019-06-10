package au.id.tmm.ausvotes.data_sources.common.streaming

import java.io.{IOException, InputStream}
import java.nio.file.{Files, Path}

import au.id.tmm.bfect.effects.Sync
import au.id.tmm.bfect.effects.Sync.Ops

object OpenAfterDownload {

  def downloadAndOpen[F[+_, +_] : Sync](
                                         makeDownloadedInputStream: F[IOException, InputStream],
                                         destination: Path,
                                         replaceExisting: Boolean = false,
                                       ): F[IOException, InputStream] =
    for {
      alreadyExists <- syncCatchIOException(Files.exists(destination))
      _ <- if (alreadyExists && !replaceExisting) Sync.unit else Downloading.downloadToPath(makeDownloadedInputStream, destination)
      streamFromDownloaded <- OpenFile.openFile(destination)
    } yield streamFromDownloaded

}
