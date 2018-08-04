package au.id.tmm.ausvotes.tasks.generatepreferencetrees

import java.io.{Closeable, IOException, OutputStream}
import java.nio.file.{Files, Path}

import scalaz.zio.IO

object CloseableIO {

  def bracket[E, A <: Closeable, B](acquire: IO[E, A])(use: A => IO[E, B]): IO[E, B] =
    IO.bracket(acquire)(closeable => IO.sync(closeable.close()))(use)

  def outputStreamFor(path: Path): IO[Exception, OutputStream] = IO.syncCatch(Files.newOutputStream(path)) {
    case e: IOException => e
  }

}
