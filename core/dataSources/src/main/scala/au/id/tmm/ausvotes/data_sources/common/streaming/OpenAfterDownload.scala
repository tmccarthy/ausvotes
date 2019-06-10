package au.id.tmm.ausvotes.data_sources.common.streaming

import java.io.{IOException, InputStream}
import java.net.URL
import java.nio.file.{Files, InvalidPathException, Path}

import au.id.tmm.bfect.effects.Sync
import au.id.tmm.bfect.effects.Sync.Ops
import org.apache.commons.io.FilenameUtils

object OpenAfterDownload {

  def downloadAndOpen[F[+_, +_] : Sync](
                                         url: URL,
                                         destinationBasePath: Path,
                                         replaceExisting: Boolean = false,
                                       ): F[IOException, InputStream] =
    for {
      destination <- Sync.syncException(destinationBasePath.resolve(FilenameUtils.getName(url.getPath))).refineOrDie {
        case e: InvalidPathException => new IOException(e)
      }

      inputStream <- copyAndOpen(OpenUrl.openUrl(url), destination, replaceExisting)
    } yield inputStream

  def copyAndOpen[F[+_, +_] : Sync](
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
