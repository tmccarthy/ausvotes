package au.id.tmm.ausvotes.data_sources.common.streaming

import java.io.{IOException, InputStream}
import java.nio.file.{Files, Path}

import au.id.tmm.bfect.effects.Sync

object OpenFile {

  def openFile[F[+_, +_] : Sync](path: Path): F[IOException, InputStream] =
    syncCatchIOException(Files.newInputStream(path))

}
