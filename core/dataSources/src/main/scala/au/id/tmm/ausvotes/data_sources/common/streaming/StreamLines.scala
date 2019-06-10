package au.id.tmm.ausvotes.data_sources.common.streaming

import java.io.{IOException, InputStream}
import java.nio.charset.Charset

import au.id.tmm.bfect.catsinterop._
import au.id.tmm.bfect.effects.Sync

import scala.io.Source

object StreamLines {

  def streamLines[F[+_, +_] : Sync](makeInputStream: F[IOException, InputStream], charset: Charset = defaultCharset): F[IOException, fs2.Stream[F[Throwable, +?], String]] =
    Sync.bracketCloseable(makeInputStream) { inputStream =>
      val source = Source.fromInputStream(inputStream)

      syncCatchIOException(fs2.Stream.fromIterator(source.getLines()))
    }


}
