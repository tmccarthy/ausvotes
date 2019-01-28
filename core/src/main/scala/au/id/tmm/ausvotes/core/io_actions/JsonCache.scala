package au.id.tmm.ausvotes.core.io_actions

import au.id.tmm.ausvotes.model.ExceptionCaseClass
import io.circe.{Decoder, Encoder}
import scalaz.zio.IO

trait JsonCache[F[+_, +_]] {

  def getOrCompute[K : Encoder, V : Encoder : Decoder, E](key: K)(compute: IO[Exception, V]): F[JsonCache.Error, V]

  def get[K : Encoder, V : Decoder](key: K): F[JsonCache.Error, Option[V]]

  def put[K : Encoder, V : Encoder](key: K, value: V): F[JsonCache.Error, Unit]

}

object JsonCache {

  def getOrCompute[F[+_, +_] : JsonCache, K : Encoder, V : Encoder : Decoder, E](key: K)(compute: IO[Exception, V]): F[JsonCache.Error, V] =
    implicitly[JsonCache[F]].getOrCompute[K, V, E](key)(compute)

  def get[F[+_, +_] : JsonCache, K : Encoder, V : Decoder](key: K): F[JsonCache.Error, Option[V]] =
    implicitly[JsonCache[F]].get(key)

  def put[F[+_, +_] : JsonCache, K : Encoder, V : Encoder](key: K, value: V): F[JsonCache.Error, Unit] =
    implicitly[JsonCache[F]].put(key, value)


  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

}
