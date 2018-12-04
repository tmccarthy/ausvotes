package au.id.tmm.ausvotes.shared.io.typeclasses

import cats.{Bifunctor, Monad}

trait BifunctorMonad[F[+_, +_]] extends Bifunctor[F] {

  def pure[A](a: A): F[Nothing, A]
  def leftPure[E](e: E): F[E, Nothing]
  def unit: F[Nothing, Unit] = pure(Unit)

  def fromEither[E, A](either: Either[E, A]): F[E, A] = either match {
    case Right(a) => pure(a)
    case Left(e) => leftPure(e)
  }

  def map[E, A, B](fea: F[E, A])(fab: A => B): F[E, B]

  override def leftMap[E1, A, E2](fea: F[E1, A])(f: E1 => E2): F[E2, A]

  override def bimap[A, B, C, D](fab: F[A, B])(f: A => C, g: B => D): F[C, D]

  def flatten[E1, E2 >: E1, A](fefa: F[E1, F[E2, A]]): F[E2, A] = flatMap[E1, E2, F[E2, A], A](fefa)(identity)

  def flatMap[E1, E2 >: E1, A, B](fe1a: F[E1, A])(fafe2b: A => F[E2, B]): F[E2, B]

  /**
    * Keeps calling `f` until a `scala.util.Right[B]` is returned.
    */
  def tailRecM[E, A, A1](a: A)(f: A => F[E, Either[A, A1]]): F[E, A1]

}

object BifunctorMonad {

  implicit def bifunctorMonadIsAMonad[F[+_, +_] : BifunctorMonad, E]: Monad[F[E, +?]] = new Monad[F[E, +?]] {
    private val bifunctorMonad = implicitly[BifunctorMonad[F]]

    override def flatMap[A, A1](fea: F[E, A])(f: A => F[E, A1]): F[E, A1] = bifunctorMonad.flatMap[E, E, A, A1](fea)(f)

    override def tailRecM[A, A1](a: A)(f: A => F[E, Either[A, A1]]): F[E, A1] = bifunctorMonad.tailRecM[E, A, A1](a)(f)

    override def pure[A](a: A): F[E, A] = bifunctorMonad.pure(a)
  }

}
