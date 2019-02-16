package au.id.tmm.ausvotes.shared.io.typeclasses

import cats.MonadError

import scala.util.{Failure, Success, Try}

trait BifunctorMonadError[F[+_, +_]] extends BifunctorMonad[F] {

  def raiseError[E](e: E): F[E, Nothing] = leftPure(e)

  def handleErrorWith[E, A, E1](fea: F[E, A])(f: E => F[E1, A]): F[E1, A]

  def recoverWith[E, A, E1 >: E](fea: F[E, A])(pf: PartialFunction[E, F[E1, A]]): F[E1, A] = {
    val completeErrorHandler: E => F[E1, A] = e => pf.applyOrElse(e, raiseError[E1](_))

    handleErrorWith[E, A, E1](fea)(completeErrorHandler)
  }

  def catchLeft[E, A, E1 >: E](fea: F[E, A], pf: PartialFunction[E, F[E1, A]]): F[E1, A] =
    recoverWith[E, A, E1](fea)(pf)

  def attempt[E, A](fea: F[E, A]): F[Nothing, Either[E, A]] =
    handleErrorWith(
      map(fea)(Right(_): Either[E, A])
    )(e => pure(Left(e)))

  def absolve[E, A](feEitherEA: F[E, Either[E, A]]): F[E, A] =
    flatMap(feEitherEA) {
      case Right(a) => pure(a): F[E, A]
      case Left(e) => leftPure(e): F[E, A]
    }

  def fromTry[A](t: Try[A]): F[Throwable, A] =
    t match {
      case Success(a) => pure(a)
      case Failure(e) => raiseError(e)
    }

}

object BifunctorMonadError {

  def apply[F[+_, +_] : BifunctorMonadError]: BifunctorMonadError[F] = implicitly[BifunctorMonadError[F]]

  def pure[F[+_, +_] : BifunctorMonadError, A](a: A): F[Nothing, A] = implicitly[BifunctorMonadError[F]].pure(a)
  def leftPure[F[+_, +_] : BifunctorMonadError, E](e: E): F[E, Nothing] = implicitly[BifunctorMonadError[F]].leftPure(e)

  def fromEither[F[+_, +_] : BifunctorMonadError, E, A](either: Either[E, A]): F[E, A] = implicitly[BifunctorMonadError[F]].fromEither(either)

  def unit[F[+_, +_] : BifunctorMonadError]: F[Nothing, Unit] = implicitly[BifunctorMonadError[F]].unit

  implicit class Ops[F[+_, +_] : BifunctorMonadError, E, A](fea: F[E, A]) {
    def map[B](f: A => B): F[E, B] = implicitly[BifunctorMonadError[F]].map(fea)(f)
    def leftMap[E2](f: E => E2): F[E2, A] = implicitly[BifunctorMonadError[F]].leftMap(fea)(f)
    def flatMap[E2 >: E, B](fafe2b: A => F[E2, B]): F[E2, B] = implicitly[BifunctorMonadError[F]].flatMap[E, E2, A, B](fea)(fafe2b)

    def attempt: F[Nothing, Either[E, A]] = implicitly[BifunctorMonadError[F]].attempt(fea)
    def catchLeft[E1 >: E](pf: PartialFunction[E, F[E1, A]]): F[E1, A] =
      implicitly[BifunctorMonadError[F]].catchLeft(fea, pf)
  }

  implicit class FlattenOps[F[+_, +_] : BifunctorMonadError, E1, E2 >: E1, A](fe1fe2a: F[E1, F[E2, A]]) {
    def flatten: F[E2, A] = implicitly[BifunctorMonadError[F]].flatten(fe1fe2a)
  }

  implicit class EitherOps[F[+_, +_] : BifunctorMonadError, E, A](feea: F[E, Either[E, A]]) {
    def absolve: F[E, A] = implicitly[BifunctorMonadError[F]].absolve(feea)
  }

  implicit def bifunctorMonadErrorIsAMonadError[E, F[+_, +_] : BifunctorMonadError]: MonadError[F[E, +?], E] = new MonadError[F[E, +?], E] {
    private val biFunctorMonadError = implicitly[BifunctorMonadError[F]]

    override def flatMap[A, A1](fea: F[E, A])(f: A => F[E, A1]): F[E, A1] = biFunctorMonadError.flatMap[E, E, A, A1](fea)(f)

    override def tailRecM[A, A1](a: A)(f: A => F[E, Either[A, A1]]): F[E, A1] = biFunctorMonadError.tailRecM[E, A, A1](a)(f)

    override def pure[A](a: A): F[E, A] = biFunctorMonadError.pure(a)

    override def raiseError[A](e: E): F[E, A] = biFunctorMonadError.leftPure(e)

    override def handleErrorWith[A](fea: F[E, A])(f: E => F[E, A]): F[E, A] = biFunctorMonadError.handleErrorWith(fea)(f)
  }

}
