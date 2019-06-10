package au.id.tmm.ausvotes.data_sources.common.streaming

import java.io.{IOException, InputStream}
import java.nio.file.Path
import java.util.zip.{ZipEntry, ZipFile}

import au.id.tmm.bfect.effects.Sync
import au.id.tmm.bfect.effects.Sync.Ops

object OpenZip {

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

}
