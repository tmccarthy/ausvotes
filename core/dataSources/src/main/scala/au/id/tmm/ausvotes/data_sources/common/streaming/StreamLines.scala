package au.id.tmm.ausvotes.data_sources.common.streaming

import java.io.{IOException, InputStream}
import java.nio.charset.Charset

import au.id.tmm.bfect.catsinterop._
import au.id.tmm.bfect.effects.Sync
import au.id.tmm.bfect.effects.Sync.Ops

import scala.io.Source

object StreamLines {

  def streamLines[F[+_, +_] : Sync](
                                     makeInputStream: F[IOException, InputStream],
                                     charset: Charset = defaultCharset,
                                   ): F[IOException, fs2.Stream[F[Throwable, +?], String]] =
    for {
      inputStream <- makeInputStream
      source = Source.fromInputStream(inputStream)
      stream <- syncCatchIOException(fs2.Stream.fromIterator(source.getLines()).onFinalize(Sync.sync(source.close())))
    } yield stream

}
