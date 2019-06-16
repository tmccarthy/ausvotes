package au.id.tmm.ausvotes.data_sources.common.streaming

import java.io.IOException
import java.net.URL
import java.nio.file.Path

import au.id.tmm.ausvotes.data_sources.common.streaming.OpeningInputStreams._
import au.id.tmm.ausvotes.data_sources.common.streaming.ReadingInputStreams._
import au.id.tmm.bfect.effects.Sync
import au.id.tmm.bfect.effects.Sync.Ops

object MakeSource {

  def withUrlFrom[F[+_, +_] : Sync, K](
    downloadDirectory: Path,
    replaceExisting: Boolean,
  )(urlFromK: K => F[Exception, URL]): MakeSource[F, IOException, K] = key =>
    for {
      url       <- urlFromK(key).leftMap(new IOException(_))
      localPath <- downloadToDirectory(url, downloadDirectory, replaceExisting)
      source    <- streamLines(openFile(localPath))
    } yield source

  def withZipUrlFrom[F[+_, +_] : Sync, K](
    downloadDirectory: Path,
    replaceExisting: Boolean,
  )(urlAndZipEntryFromK: K => F[Exception, (URL, String)]): MakeSource[F, IOException, K] = key =>
    for {
      urlAndZipEntryName <- urlAndZipEntryFromK(key).leftMap(new IOException(_))
      url                 = urlAndZipEntryName._1
      zipEntryName        = urlAndZipEntryName._2

      localZipFilePath   <- downloadToDirectory(url, downloadDirectory, replaceExisting)
      source             <- streamLines(openZipEntry(localZipFilePath, zipEntryName))
    } yield source

}
