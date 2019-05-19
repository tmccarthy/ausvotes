package au.id.tmm.ausvotes.shared.io

import java.io.{IOException, InputStream}
import java.nio.charset.Charset

import au.id.tmm.bfect.effects.Sync
import au.id.tmm.bfect.ziointerop._
import org.apache.commons.io.IOUtils
import scalaz.zio.IO

object Closeables {

  object InputStreams {
    def readAsString(inputStream: IO[IOException, InputStream], charset: Charset): IO[IOException, String] =
      Sync[IO].bracketCloseable(inputStream) { is =>
        IO.effect(IOUtils.toString(is, charset)).refineOrDie {
          case e: IOException => e
        }
      }
  }

}
