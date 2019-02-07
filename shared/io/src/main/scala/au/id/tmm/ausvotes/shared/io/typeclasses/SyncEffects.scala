package au.id.tmm.ausvotes.shared.io.typeclasses

import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError.Ops
import cats.effect.{ExitCase, Sync}

trait SyncEffects[F[+_, +_]] extends BifunctorMonadError[F] {
  def sync[A](effect: => A): F[Nothing, A]
  def syncException[A](effect: => A): F[Exception, A]
  def syncCatch[E, A](effect: => A)(f: PartialFunction[Throwable, E]): F[E, A]
  def syncThrowable[A](effect: => A): F[Throwable, A]

  def bracketCase[A, B](acquire: F[Throwable, A])(use: A => F[Throwable, B])(release: (A, ExitCase[Throwable]) => F[Throwable, Unit]): F[Throwable, B]
}

object SyncEffects {
  def sync[F[+_, +_] : SyncEffects, A](effect: => A): F[Nothing, A] =
    implicitly[SyncEffects[F]].sync(effect)
  def syncException[F[+_, +_] : SyncEffects, A](effect: => A): F[Exception, A] =
    implicitly[SyncEffects[F]].syncException(effect)
  def syncCatch[F[+_, +_] : SyncEffects, E, A](effect: => A)(f: PartialFunction[Throwable, E]): F[E, A] =
    implicitly[SyncEffects[F]].syncCatch(effect)(f)
  def syncThrowable[F[+_, +_] : SyncEffects, A](effect: => A): F[Throwable, A] =
    implicitly[SyncEffects[F]].syncThrowable(effect)

  implicit def catsSyncForSyncEffects[F[+_, +_] : SyncEffects]: Sync[F[Throwable, +?]] = new Sync[F[Throwable, +?]] {

    override def suspend[A](thunk: => F[Throwable, A]): F[Throwable, A] = (BifunctorMonadError.unit: F[Throwable, Unit]).flatMap(_ => thunk)

    override def bracketCase[A, B](acquire: F[Throwable, A])(use: A => F[Throwable, B])(release: (A, ExitCase[Throwable]) => F[Throwable, Unit]): F[Throwable, B] =
      implicitly[SyncEffects[F]].bracketCase(acquire)(use)(release)

    override def pure[A](x: A): F[Throwable, A] = BifunctorMonadError.pure(x)

    override def flatMap[A, B](fa: F[Throwable, A])(f: A => F[Throwable, B]): F[Throwable, B] = fa.flatMap(f)

    override def tailRecM[A, B](a: A)(f: A => F[Throwable, Either[A, B]]): F[Throwable, B] = implicitly[BifunctorMonadError[F]].tailRecM(a)(f)

    override def raiseError[A](e: Throwable): F[Throwable, A] = BifunctorMonadError.leftPure(e)

    override def handleErrorWith[A](fa: F[Throwable, A])(f: Throwable => F[Throwable, A]): F[Throwable, A] = implicitly[BifunctorMonadError[F]].handleErrorWith(fa)(f)

  }
}
