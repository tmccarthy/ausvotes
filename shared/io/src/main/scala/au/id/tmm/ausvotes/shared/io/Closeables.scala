package au.id.tmm.ausvotes.shared.io

import java.io.{Closeable, IOException, InputStream}
import java.nio.charset.Charset

import org.apache.commons.io.IOUtils
import scalaz.zio.IO

object Closeables {

  def bracketCloseable[R <: Closeable, E, A](acquire: IO[E, R])(use: R => IO[E, A]): IO[E, A] =
    IO.bracket(acquire)(release = resource => IO.sync(resource.close()))(use)

  object InputStreams {
    def readAsString(inputStream: IO[IOException, InputStream], charset: Charset): IO[IOException, String] =
      bracketCloseable(inputStream) { is =>
        IO.syncCatch(IOUtils.toString(is, charset)) {
          case e: IOException => e
        }
      }
  }

}
