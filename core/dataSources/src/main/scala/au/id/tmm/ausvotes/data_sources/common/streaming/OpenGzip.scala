package au.id.tmm.ausvotes.data_sources.common.streaming

import java.io.{IOException, InputStream}
import java.util.zip.GZIPInputStream

import au.id.tmm.bfect.effects.Sync
import au.id.tmm.bfect.effects.Sync.Ops

object OpenGzip {

  def openGzip[F[+_, +_] : Sync](makeUnderlyingInputStream: F[IOException, InputStream]): F[IOException, GZIPInputStream] =
    for {
      underlyingInputStream <- makeUnderlyingInputStream
      gzipStream <- syncCatchIOException(new GZIPInputStream(underlyingInputStream))
    } yield gzipStream

}
