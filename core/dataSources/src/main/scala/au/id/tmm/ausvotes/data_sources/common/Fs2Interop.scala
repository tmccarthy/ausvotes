package au.id.tmm.ausvotes.data_sources.common

import au.id.tmm.bfect.BME
import au.id.tmm.bfect.BME._
import au.id.tmm.bfect.catsinterop._
import au.id.tmm.bfect.effects.Sync
import fs2.Stream.Compiler
import scalaz.zio.IO

object Fs2Interop {

  implicit def syncCompiler[F[+_, +_] : Sync]: Compiler[F[Throwable, +?], F[Throwable, +?]] =
    Compiler.syncInstance[F[Throwable, +?]]

  implicit val zioCompiler: Compiler[IO[Throwable, +?], IO[Throwable, +?]] =
    Compiler.syncInstance[IO[Throwable, +?]](scalaz.zio.interop.catz.taskConcurrentInstances)

  implicit class ThrowableEOps[F[+_, +_] : BME, A](fThrowableA: F[Throwable, A]) {

    def swallowThrowables: F[Exception, A] = implicitly[BME[F]].handleErrorWith(fThrowableA) {
      case e: Exception => BME.leftPure(e)
      case t: Throwable => throw t
    }

    def swallowThrowablesAndWrapIn[E](wrap: Exception => E): F[E, A] = swallowThrowables.leftMap(wrap)

  }

}
