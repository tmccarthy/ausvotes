package au.id.tmm.ausvotes.core.io_actions

import au.id.tmm.ausvotes.model.ExceptionCaseClass
import io.circe.{Decoder, Encoder}
import scalaz.zio.IO

trait JsonCache[F[+_, +_]] {

  def get[K : Encoder, V : Encoder : Decoder, E](key: K)(compute: IO[Exception, V]): F[JsonCache.Error, V]

}

object JsonCache {

  def get[F[+_, +_] : JsonCache, K : Encoder, V : Encoder : Decoder, E](key: K)(compute: IO[Exception, V]): F[JsonCache.Error, V] =
    implicitly[JsonCache[F]].get[K, V, E](key)(compute)

  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

}
