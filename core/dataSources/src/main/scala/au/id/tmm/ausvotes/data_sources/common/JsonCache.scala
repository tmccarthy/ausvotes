package au.id.tmm.ausvotes.data_sources.common

import java.nio.charset.Charset
import java.nio.file.{Files, Path}

import au.id.tmm.ausvotes.model.ExceptionCaseClass
import au.id.tmm.bfect.BME
import au.id.tmm.bfect.catsinterop._
import au.id.tmm.bfect.effects.Sync
import au.id.tmm.bfect.effects.Sync._
import au.id.tmm.utilities.codec.digest._
import au.id.tmm.utilities.codec.binarycodecs._
import cats.instances.option._
import cats.syntax.traverse._
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder}

// TODO move this to shared io project
trait JsonCache[F[+_, +_]] {

  def getOrCompute[K : Encoder, V : Encoder : Decoder, E](key: K)(compute: F[Exception, V]): F[JsonCache.Error, V]

  def get[K : Encoder, V : Decoder](key: K): F[JsonCache.Error, Option[V]]

  def put[K : Encoder, V : Encoder](key: K, value: V): F[JsonCache.Error, Unit]

}

object JsonCache {

  def getOrCompute[F[+_, +_] : JsonCache, K : Encoder, V : Encoder : Decoder, E](key: K)(compute: F[Exception, V]): F[JsonCache.Error, V] =
    implicitly[JsonCache[F]].getOrCompute[K, V, E](key)(compute)

  def get[F[+_, +_] : JsonCache, K : Encoder, V : Decoder](key: K): F[JsonCache.Error, Option[V]] =
    implicitly[JsonCache[F]].get(key)

  def put[F[+_, +_] : JsonCache, K : Encoder, V : Encoder](key: K, value: V): F[JsonCache.Error, Unit] =
    implicitly[JsonCache[F]].put(key, value)


  final case class Error(cause: Exception) extends ExceptionCaseClass with ExceptionCaseClass.WithCause

  // TODO handle the race conditions
  final class OnDisk[F[+_, +_] : Sync] private (location: Path) extends JsonCache[F] {

    private val charset = Charset.forName("UTF-8")

    private def recordPathOf[K: Encoder](key: K): F[JsonCache.Error, Path] = {
      val keyJson = key.asJson
      val keyDigest = keyJson.noSpaces.sha256

      Sync.syncException(location.resolve(s"${keyDigest.asHexString}.json"))
        .leftMap(JsonCache.Error)
    }

    override def get[K: Encoder, V: Decoder](key: K): F[JsonCache.Error, Option[V]] =
      for {
        recordPath <- recordPathOf(key)

        recordExists <- Sync.syncException(Files.exists(recordPath))
          .leftMap(JsonCache.Error)

        record <- Some(readRecordAt(recordPath)).filter(_ => recordExists).sequence
      } yield record

    override def put[K: Encoder, V: Encoder](key: K, value: V): F[JsonCache.Error, Unit] =
      for {
        recordPath <- recordPathOf(key)
        _ <- writeRecord(value, recordPath)
      } yield ()

    override def getOrCompute[K: Encoder, V: Encoder : Decoder, E](key: K)(compute: F[Exception, V]): F[JsonCache.Error, V] =
      for {
        existingRecord <- get(key)
        record <- existingRecord match {
          case Some(value) => BME.pure(value)
          case None =>
            for {
              computedValue <- compute.leftMap(JsonCache.Error)
              _ <- put(key, computedValue)
            } yield computedValue
        }
      } yield record

    private def readRecordAt[V : Decoder](path: Path): F[JsonCache.Error, V] = {
      for {
        contentsAsString <- Sync.syncException(new String(Files.readAllBytes(path), charset))
        contentsAsJson <- BME.fromEither(io.circe.parser.parse(contentsAsString))
        record <- BME.fromEither(implicitly[Decoder[V]].decodeJson(contentsAsJson))
      } yield record
    }.leftMap(JsonCache.Error)

    private def writeRecord[V : Encoder](record: V, path: Path): F[JsonCache.Error, Unit] = {
      val fileContents = record.asJson.noSpaces

      for {
        _ <- Sync.syncException(Files.createDirectories(path.getParent))
        _ <- Sync.syncException(Files.deleteIfExists(path))
        _ <- Sync.syncException(Files.write(path, java.util.Arrays.asList(fileContents), charset))
      } yield ()
    }.leftMap(JsonCache.Error)
  }

  object OnDisk {
    def apply[F[+_, +_] : Sync](location: Path): OnDisk[F] = new OnDisk(location)
  }

}
