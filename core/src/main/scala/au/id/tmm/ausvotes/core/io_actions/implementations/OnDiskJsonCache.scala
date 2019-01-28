package au.id.tmm.ausvotes.core.io_actions.implementations

import java.nio.charset.Charset
import java.nio.file.{Files, Path}

import au.id.tmm.ausvotes.core.io_actions.JsonCache
import au.id.tmm.ausvotes.shared.io.instances.ZIOInstances._
import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError._
import au.id.tmm.utilities.hashing.StringHashing.StringHashingImplicits
import cats.instances.option._
import cats.syntax.traverse._
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder}
import scalaz.zio.IO

// TODO handle the race conditions
final class OnDiskJsonCache(location: Path) extends JsonCache[IO] {

  private val charset = Charset.forName("UTF-8")

  private def recordPathOf[K: Encoder](key: K): IO[JsonCache.Error, Path] = {
    val keyJson = key.asJson
    val keyDigest = keyJson.noSpaces.sha256checksum(charset)

    IO.syncException(location.resolve(s"${keyDigest.asHexString}.json"))
      .leftMap(JsonCache.Error)
  }

  override def get[K: Encoder, V: Decoder](key: K): IO[JsonCache.Error, Option[V]] =
    for {
      recordPath <- recordPathOf(key)

      recordExists <- IO.syncException(Files.exists(recordPath))
        .leftMap(JsonCache.Error)

      record <- Some(readRecordAt(recordPath)).filter(_ => recordExists).sequence
    } yield record

  override def put[K: Encoder, V: Encoder](key: K, value: V): IO[JsonCache.Error, Unit] =
    for {
      recordPath <- recordPathOf(key)
      _ <- writeRecord(value, recordPath)
    } yield ()

  override def getOrCompute[K: Encoder, V: Encoder : Decoder, E](key: K)(compute: IO[Exception, V]): IO[JsonCache.Error, V] =
    for {
      existingRecord <- get(key)
      record <- existingRecord match {
        case Some(value) => IO.point(value)
        case None =>
          for {
            computedValue <- compute.leftMap(JsonCache.Error)
            _ <- put(key, computedValue)
          } yield computedValue
      }
    } yield record

  private def readRecordAt[V : Decoder](path: Path): IO[JsonCache.Error, V] = {
    for {
      contentsAsString <- IO.syncException(new String(Files.readAllBytes(path), charset))
      contentsAsJson <- IO.fromEither(io.circe.parser.parse(contentsAsString))
      record <- IO.fromEither(implicitly[Decoder[V]].decodeJson(contentsAsJson))
    } yield record
  }.leftMap(JsonCache.Error)

  private def writeRecord[V : Encoder](record: V, path: Path): IO[JsonCache.Error, Unit] = {
    val fileContents = record.asJson.noSpaces

    for {
      _ <- IO.syncException(Files.createDirectories(path.getParent))
      _ <- IO.syncException(Files.deleteIfExists(path))
      _ <- IO.syncException(Files.write(path, java.util.Arrays.asList(fileContents), charset))
    } yield ()
  }.leftMap(JsonCache.Error)
}
