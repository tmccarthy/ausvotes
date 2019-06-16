package au.id.tmm.ausvotes.data_sources.common

import java.io.IOException
import java.nio.charset.Charset

import au.id.tmm.bfect.effects.Sync

package object streaming {
  val defaultCharset: Charset = Charset.forName("UTF-8")

  private[streaming] def syncCatchIOException[F[+_, +_] : Sync, A](effect: => A): F[IOException, A] = Sync.syncCatch(effect) {
    case e: IOException => e
  }

  type Source[F[+_, +_]] = fs2.Stream[F[Throwable, +?], String]
  type MakeSource[F[+_, +_], E, K] = K => F[E, Source[F]]

}
