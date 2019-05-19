package au.id.tmm.ausvotes.data_sources.common

import java.io.{IOException, InputStream}
import java.net.URL
import java.nio.charset.Charset
import java.nio.file.Path
import java.util.zip.{GZIPInputStream, ZipEntry, ZipFile}

import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.bfect.BME
import au.id.tmm.bfect.effects.Sync
import au.id.tmm.bfect.effects.Sync._
import org.apache.commons.io.FilenameUtils

import scala.io.Source

abstract class MakeSource[F[+_, +_] : Sync, +E <: Exception, -A] {

  def makeSourceFor(a: A): F[E, Source]

  def mappingErrors[E2 <: Exception](convertError: E => E2): MakeSource[F, E2, A] =
    new MakeSource[F, E2, A] {
      override def makeSourceFor(a: A): F[E2, Source] = MakeSource.this.makeSourceFor(a).leftMap(convertError)
    }

  def butFirst[B, E2 >: E <: Exception](f: B => F[E2, A]): MakeSource[F, E2, B] =
    new MakeSource[F, E2, B] {
      override def makeSourceFor(b: B): F[E2, Source] =
        for {
          a <- f(b)

          source <- MakeSource.this.makeSourceFor(a)
        } yield source
    }

}

//noinspection ConvertibleToMethodValue
object MakeSource {

  private val defaultCharset: Charset = Charset.forName("UTF-8")

  private def syncCatchIOException[F[+_, +_] : Sync, A](effect: => A) = Sync.syncCatch(effect) {
    case e: IOException => e
  }

  def never[F[+_, +_] : Sync, A]: MakeSource[F, UnsupportedOperationException, A] = new MakeSource[F, UnsupportedOperationException, A] {
    override def makeSourceFor(a: A): F[UnsupportedOperationException, Source] = BME.leftPure(new UnsupportedOperationException)
  }

  def fromFile[F[+_, +_] : Sync](charset: Charset = defaultCharset): MakeSource[F, IOException, Path] =
    new MakeSource[F, IOException, Path] {
      override def makeSourceFor(path: Path): F[IOException, Source] = syncCatchIOException(Source.fromFile(path.toFile, charset.toString))
    }

  private def downloadUrlTo[F[+_, +_] : Sync : DownloadToPath](pathForDownloads: Path)(url: URL): F[IOException, Path] =
    for {
      targetPath <- syncCatchIOException(pathForDownloads.resolve(FilenameUtils.getName(url.getPath)))
      _ <- DownloadToPath.downloadToPath(url, targetPath)
    } yield targetPath

  def fromDownloadedFile[F[+_, +_] : Sync : DownloadToPath](pathForDownloads: Path, charset: Charset = defaultCharset): MakeSource[F, IOException, URL] =
    fromFile[F](charset).butFirst(downloadUrlTo(pathForDownloads)(_))

  def fromInputStream[F[+_, +_] : Sync](charset: Charset = defaultCharset): MakeSource[F, IOException, InputStream] =
    new MakeSource[F, IOException, InputStream] {
      override def makeSourceFor(inputStream: InputStream): F[IOException, Source] = syncCatchIOException(Source.fromInputStream(inputStream, charset.toString))
    }

  def fromGzipStream[F[+_, +_] : Sync](charset: Charset = defaultCharset): MakeSource[F, IOException, InputStream] =
    fromInputStream[F](charset).butFirst(inputStream => syncCatchIOException(new GZIPInputStream(inputStream)))

  private def openResource[F[+_, +_] : Sync](resourceName: String): F[FromResource.Error, InputStream] =
    for {
      maybeInputStream <- Sync.sync(Option(getClass.getResourceAsStream(resourceName))): F[FromResource.Error, Option[InputStream]]

      inputStream <- maybeInputStream match {
        case Some(inputStream) => BME.pure(inputStream)
        case None => BME.leftPure(FromResource.Error.NoResourceFound)
      }
    } yield inputStream

  object FromResource {
    sealed abstract class Error extends ExceptionCaseClass

    object Error {
      final case class AnIOException(cause: IOException) extends Error with ExceptionCaseClass.WithCause
      case object NoResourceFound extends Error
    }
  }

  def fromResource[F[+_, +_] : Sync](charset: Charset = defaultCharset): MakeSource[F, FromResource.Error, String] =
    fromInputStream[F](charset)
      .mappingErrors(FromResource.Error.AnIOException)
      .butFirst(openResource(_))

  def fromGzipResource[F[+_, +_] : Sync](charset: Charset = defaultCharset): MakeSource[F, FromResource.Error, String] =
    fromGzipStream[F](charset)
      .mappingErrors(FromResource.Error.AnIOException)
      .butFirst(openResource(_))

  private def openZipEntry[F[+_, +_] : Sync](args: (Path, FromZipFile.ZipEntryName)): F[FromZipFile.Error, InputStream] = {
    val (zipFilePath, zipEntryName) = args

    for {
      zipFile <- syncCatchIOException(new ZipFile(zipFilePath.toFile))
        .leftMap(FromZipFile.Error.AnIOException)

      maybeZipEntry <- Sync.syncCatch(Option(zipFile.getEntry(zipEntryName.asString))) {
        case e: IllegalStateException => FromZipFile.Error.ZipFileUnexpectedlyClosed(e)
      }

      zipEntry <- maybeZipEntry match {
        case Some(zipEntry) => BME.pure(zipEntry): F[FromZipFile.Error, ZipEntry]
        case None => BME.leftPure(FromZipFile.Error.ZipEntryNotFound(zipEntryName)): F[FromZipFile.Error, ZipEntry]
      }

      inputStream <- syncCatchIOException(zipFile.getInputStream(zipEntry))
        .leftMap(FromZipFile.Error.AnIOException)

    } yield inputStream
  }

  object FromZipFile {

    final case class ZipEntryName(asString: String) extends AnyVal

    sealed abstract class Error extends ExceptionCaseClass

    object Error {
      final case class AnIOException(cause: IOException) extends Error with ExceptionCaseClass.WithCause
      final case class ZipEntryNotFound(zipEntryName: ZipEntryName) extends Error
      final case class ZipFileUnexpectedlyClosed(cause: IllegalStateException) extends Error with ExceptionCaseClass.WithCause
    }

  }

  def fromZipFile[F[+_, +_] : Sync](charset: Charset = defaultCharset): MakeSource[F, FromZipFile.Error, (Path, FromZipFile.ZipEntryName)] =
    fromInputStream(charset)
      .mappingErrors(FromZipFile.Error.AnIOException)
      .butFirst(openZipEntry(_))

  object FromDownloadedZipFile {

    sealed abstract class Error extends ExceptionCaseClass

    object Error {
      final case class DownloadError(cause: IOException) extends Error with ExceptionCaseClass.WithCause
      final case class ZipFileReadError(cause: FromZipFile.Error) extends Error with ExceptionCaseClass.WithCause
    }
  }

  def fromDownloadedZipFile[F[+_, +_] : Sync : DownloadToPath](pathForDownloads: Path, charset: Charset = defaultCharset): MakeSource[F, FromDownloadedZipFile.Error, (URL, FromZipFile.ZipEntryName)] =
    fromZipFile(charset)
      .mappingErrors(FromDownloadedZipFile.Error.ZipFileReadError)
      .butFirst { case (url, zipEntryName) =>
        for {
          localPath <- downloadUrlTo(pathForDownloads)(url)
            .leftMap(FromDownloadedZipFile.Error.DownloadError)
        } yield (localPath, zipEntryName)
      }

}
