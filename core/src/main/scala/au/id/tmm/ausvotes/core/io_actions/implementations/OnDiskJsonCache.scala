package au.id.tmm.ausvotes.core.io_actions.implementations

import java.nio.charset.Charset
import java.nio.file.{Files, Path}

import au.id.tmm.ausvotes.core.io_actions.JsonCache
import au.id.tmm.utilities.hashing.StringHashing.StringHashingImplicits
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder}
import scalaz.zio.IO

// TODO handle the race conditions
final class OnDiskJsonCache(location: Path) extends JsonCache[IO] {

  private val charset = Charset.forName("UTF-8")

  override def get[K: Encoder, V: Encoder : Decoder, E](key: K)(compute: IO[Exception, V]): IO[JsonCache.Error, V] = {

    val keyJson = key.asJson
    val keyDigest = keyJson.noSpaces.sha256checksum(charset)

    for {
      recordPath <- IO.syncException(location.resolve(s"${keyDigest.asHexString}.json"))

      recordExists <- IO.syncException(Files.exists(recordPath))

      record <- if (recordExists) {
        readRecordAt(recordPath)
      } else {
        for {
          record <- compute
          _ <- writeRecord(record, recordPath)
        } yield record
      }

    } yield record

  }.leftMap(JsonCache.Error)

  private def readRecordAt[V : Decoder](path: Path): IO[Exception, V] =
    for {
      contentsAsString <- IO.syncException(new String(Files.readAllBytes(path), charset))
      contentsAsJson <- IO.fromEither(io.circe.parser.parse(contentsAsString))
      record <- IO.fromEither(implicitly[Decoder[V]].decodeJson(contentsAsJson))
    } yield record

  private def writeRecord[V : Encoder](record: V, path: Path): IO[Exception, Unit] = {
    val fileContents = record.asJson.noSpaces

    for {
      _ <- IO.syncException(Files.createDirectories(path.getParent))

      _ <- IO.syncException(Files.write(path, java.util.Arrays.asList(fileContents), charset))
    } yield ()
  }
}
