package au.id.tmm.ausvotes.data_sources.common

import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError.Ops
import au.id.tmm.ausvotes.shared.io.typeclasses.{SyncEffects, BifunctorMonadError => BME}
import cats.effect.{Effect, Sync => CatsSync}
import fs2.Stream.Compiler
import scalaz.zio.IO
import scalaz.zio.interop.Task

object Fs2Interop {

  implicit def catsSyncInstance[F[+_, +_] : SyncEffects]: CatsSync[F[Throwable, +?]] = SyncEffects.catsSyncForSyncEffects[F]

  implicit val zioSyncInstance: Effect[Task] = scalaz.zio.interop.catz.taskEffectInstances

  implicit def syncCompiler[F[+_, +_] : SyncEffects]: Compiler[F[Throwable, +?], F[Throwable, +?]] =
    Compiler.syncInstance[F[Throwable, +?]](catsSyncInstance(implicitly[SyncEffects[F]]))

  implicit val zioCompiler: Compiler[IO[Throwable, +?], IO[Throwable, +?]] =
    Compiler.syncInstance[IO[Throwable, +?]](zioSyncInstance)

  implicit class ThrowableEOps[F[+_, +_] : BME, A](fThrowableA: F[Throwable, A]) {

    def swallowThrowables: F[Exception, A] = implicitly[BME[F]].handleErrorWith(fThrowableA) {
      case e: Exception => BME.leftPure(e)
      case t: Throwable => throw t
    }

    def swallowThrowablesAndWrapIn[E](wrap: Exception => E): F[E, A] = swallowThrowables.leftMap(wrap)

  }

}
