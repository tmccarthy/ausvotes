package au.id.tmm.ausvotes.shared.io.typeclasses

import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError.Ops
import au.id.tmm.ausvotes.shared.io.typeclasses.Concurrent.Fibre
import cats.effect.{CancelToken, ExitCase, Concurrent => CatsConcurrent, Fiber => CatsFibre}
import scalaz.zio.ExitResult
import scalaz.zio.ExitResult.Cause

object CatsInterop {

  def convertZioExitResultToCatsExitCase(exitResult: ExitResult[Throwable, _]): ExitCase[Throwable] =
    exitResult match {
      case ExitResult.Succeeded(value) => ExitCase.complete
      case ExitResult.Failed(cause) => convertZioFailureToCatsExitCase(cause)
    }

  @scala.annotation.tailrec
  def convertZioFailureToCatsExitCase(exitResultCause: ExitResult.Cause[Throwable]): ExitCase[Throwable] =
    exitResultCause match {
      case Cause.Checked(e) => ExitCase.Error(e)
      case Cause.Unchecked(e) => ExitCase.Error(e)
      case Cause.Interruption => ExitCase.Canceled
      case Cause.Then(left, right) => convertZioFailureToCatsExitCase(left)
      case Cause.Both(left, right) => convertZioFailureToCatsExitCase(left)
    }

  def asCatsFiber[F[+_, +_] : BifunctorMonadError, A](fibre: Fibre[F, Throwable, A]): CatsFibre[F[Throwable, +?], A] =
    new CatsFibre[F[Throwable, +?], A] {
      override def cancel: CancelToken[F[Throwable, +?]] = fibre.cancel.map(convertZioExitResultToCatsExitCase)

      override def join: F[Throwable, A] = fibre.join
    }

  implicit def catsConcurrentForConcurrent[F[+_, +_] : Concurrent]: CatsConcurrent[F[Throwable, +?]] = new CatsConcurrent[F[Throwable, +?]] {

    override def suspend[A](thunk: => F[Throwable, A]): F[Throwable, A] = (BifunctorMonadError.unit: F[Throwable, Unit]).flatMap(_ => thunk)

    override def bracketCase[A, B](acquire: F[Throwable, A])(use: A => F[Throwable, B])(release: (A, ExitCase[Throwable]) => F[Throwable, Unit]): F[Throwable, B] =
      implicitly[SyncEffects[F]].bracketCase(acquire)(use)(release)

    override def pure[A](x: A): F[Throwable, A] = BifunctorMonadError.pure(x)

    override def flatMap[A, B](fa: F[Throwable, A])(f: A => F[Throwable, B]): F[Throwable, B] = fa.flatMap(f)

    override def tailRecM[A, B](a: A)(f: A => F[Throwable, Either[A, B]]): F[Throwable, B] = implicitly[BifunctorMonadError[F]].tailRecM(a)(f)

    override def raiseError[A](e: Throwable): F[Throwable, A] = BifunctorMonadError.leftPure(e)

    override def handleErrorWith[A](fa: F[Throwable, A])(f: Throwable => F[Throwable, A]): F[Throwable, A] = implicitly[BifunctorMonadError[F]].handleErrorWith(fa)(f)

    //noinspection ConvertibleToMethodValue
    override def start[A](fa: F[Throwable, A]): F[Throwable, CatsFibre[F[Throwable, +?], A]] = implicitly[Concurrent[F]].start(fa).map(asCatsFiber(_))

    override def racePair[A, B](left: F[Throwable, A], right: F[Throwable, B]): F[Throwable, Either[(A, CatsFibre[F[Throwable, +?], B]), (CatsFibre[F[Throwable, +?], A], B)]] =
      implicitly[Concurrent[F]].racePair(left, right).map {
        case Left((a, bFibre))  => Left((a, asCatsFiber(bFibre)))
        case Right((aFibre, b)) => Right((asCatsFiber(aFibre), b))
      }

    private def eitherToF[A]: Either[Throwable, A] => F[Throwable, A] = {
      case Right(r) => BifunctorMonadError.pure(r)
      case Left(t)  => BifunctorMonadError.leftPure(t)
    }

    //noinspection ScalaUnnecessaryParentheses
    override def async[A](k: (Either[Throwable, A] => Unit) => Unit): F[Throwable, A] =
      implicitly[Concurrent[F]].async { (kk: F[Throwable, A] => Unit) =>
        k(eitherToF andThen kk)
      }

    //noinspection ScalaUnnecessaryParentheses
    override def asyncF[A](k: (Either[Throwable, A] => Unit) => F[Throwable, Unit]): F[Throwable, A] =
      implicitly[Concurrent[F]].asyncF { (kk: F[Throwable, A] => Unit) =>
        implicitly[Concurrent[F]].handleErrorWith(k(eitherToF andThen kk)) { t =>
          implicitly[Concurrent[F]].sync(throw t)
        }
      }

    override def cancelable[A](k: (Either[Throwable, A] => Unit) => CancelToken[F[Throwable, +?]]): F[Throwable, A] =
      implicitly[Concurrent[F]].cancelable[Throwable, A] { kk: (Either[Throwable, A] => Unit) =>
        k(e => kk(e)).leftMap(t => throw t)
      }

    override def race[A, B](fa: F[Throwable, A], fb: F[Throwable, B]): F[Throwable, Either[A, B]] =
      implicitly[Concurrent[F]].race(fa, fb)

  }

}
