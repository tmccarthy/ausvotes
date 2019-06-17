package au.id.tmm.ausvotes.data_sources.common.streaming

import java.io.{IOException, InputStream}
import java.net.URL
import java.nio.file.{Files, InvalidPathException, Path}
import java.util.zip.{GZIPInputStream, ZipEntry, ZipFile}

import au.id.tmm.bfect.effects.Sync
import au.id.tmm.bfect.effects.Sync.Ops
import au.id.tmm.bfect.effects.extra.Resources
import org.apache.commons.io.FilenameUtils

object OpeningInputStreams {

  def destinationInDirectory[F[+_, +_] : Sync](
                                                url: URL,
                                                destinationDirectory: Path,
                                              ): F[IOException, Path] =
    Sync.syncException(destinationDirectory.resolve(FilenameUtils.getName(url.getPath))).refineOrDie {
      case e: InvalidPathException => new IOException(e)
    }

  def streamToPath[F[+_, +_] : Sync](
                                      makeStream: F[IOException, InputStream],
                                      destination: Path,
                                      replaceExisting: Boolean,
                                    ): F[IOException, Unit] =
    for {
      alreadyExists <- syncCatchIOException(Files.exists(destination))
      _ <- if (alreadyExists && !replaceExisting) Sync.unit else
        Sync[F].bracketCloseable {
          makeStream
        } { stream =>
          syncCatchIOException(Files.copy(stream, destination))
        }.unit
    } yield ()

  def downloadToDirectory[F[+_, +_] : Sync](
                                             url: URL,
                                             destinationDirectory: Path,
                                             replaceExisting: Boolean,
                                           ): F[IOException, Path] =
    for {
      destination <- destinationInDirectory(url, destinationDirectory)

      makeStream = syncCatchIOException(url.openStream())

      _ <- streamToPath(makeStream, destination, replaceExisting)
    } yield destination

  def openFile[F[+_, +_] : Sync](path: Path): F[IOException, InputStream] =
    syncCatchIOException(Files.newInputStream(path))

  def openGzip[F[+_, +_] : Sync](makeUnderlyingInputStream: F[IOException, InputStream]): F[IOException, GZIPInputStream] =
    for {
      underlyingInputStream <- makeUnderlyingInputStream
      gzipStream <- syncCatchIOException(new GZIPInputStream(underlyingInputStream))
    } yield gzipStream

  def openZipEntry[F[+_, +_] : Sync](zipFilePath: Path, zipEntryName: String): F[IOException, InputStream] =
    for {
      zipFile <- syncCatchIOException(new ZipFile(zipFilePath.toFile))

      maybeZipEntry <- Sync.syncCatch(Option(zipFile.getEntry(zipEntryName))) {
        case e: IllegalStateException => new IOException("Zip file was unexpectedly closed" , e)
      }

      zipEntry <- maybeZipEntry match {
        case Some(zipEntry) => Sync.pure(zipEntry): F[IOException, ZipEntry]
        case None           => Sync.leftPure(new IOException(s"No zip entry of name $zipEntryName in zip file $zipFilePath")): F[IOException, ZipEntry]
      }

      inputStream <- syncCatchIOException(zipFile.getInputStream(zipEntry))
    } yield inputStream

  def openResource[F[+_, +_] : Resources](resourceName: String): F[IOException, InputStream] =
    Resources[F].getResourceAsStream(resourceName)
      .flatMap {
        case Some(is) => Sync.pure(is): F[IOException, InputStream]
        case None     => Sync.leftPure(new IOException(s"Resource $resourceName not present")): F[IOException, InputStream]
      }

}
