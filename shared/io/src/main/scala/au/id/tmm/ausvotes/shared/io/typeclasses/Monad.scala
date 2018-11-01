package au.id.tmm.ausvotes.shared.io.typeclasses

abstract class Monad[F[+_, +_]] {
  def pure[A](a: A): F[Nothing, A]
  def leftPure[E](e: E): F[E, Nothing]

  def fromEither[E, A](either: Either[E, A]): F[E, A] = either match {
    case Right(a) => pure(a)
    case Left(e) => leftPure(e)
  }

  def unit: F[Nothing, Unit] = pure(Unit)

  def map[E, A, B](fea: F[E, A])(fab: A => B): F[E, B]
  def leftMap[E1, E2, A](fea: F[E1, A])(fe1e2: E1 => E2): F[E2, A]

  def flatten[E1, E2 >: E1, A](fefa: F[E1, F[E2, A]]): F[E2, A]

  def flatMap[E1, E2 >: E1, A, B](fe1a: F[E1, A])(fafe2b: A => F[E2, B]): F[E2, B]

  def attempt[E, A](fea: F[E, A]): F[Nothing, Either[E, A]]


}

object Monad {

  def pure[F[+_, +_] : Monad, A](a: A): F[Nothing, A] = implicitly[Monad[F]].pure(a)
  def leftPure[F[+_, +_] : Monad, E](e: E): F[E, Nothing] = implicitly[Monad[F]].leftPure(e)

  def fromEither[F[+_, +_] : Monad, E, A](either: Either[E, A]): F[E, A] = implicitly[Monad[F]].fromEither(either)

  def unit[F[+_, +_] : Monad]: F[Nothing, Unit] = implicitly[Monad[F]].unit

  implicit class MonadOps[F[+_, +_] : Monad, E, A](fea: F[E, A]) {
    def map[B](f: A => B): F[E, B] = implicitly[Monad[F]].map(fea)(f)
    def leftMap[E2](f: E => E2): F[E2, A] = implicitly[Monad[F]].leftMap(fea)(f)
    def flatMap[E2 >: E, B](fafe2b: A => F[E2, B]): F[E2, B] = implicitly[Monad[F]].flatMap[E, E2, A, B](fea)(fafe2b)
    def attempt: F[Nothing, Either[E, A]] = implicitly[Monad[F]].attempt(fea)
  }

  implicit class MonadFlattenOps[F[+_, +_] : Monad, E1, E2 >: E1, A](fe1fe2a: F[E1, F[E2, A]]) {
    def flatten: F[E2, A] = implicitly[Monad[F]].flatten(fe1fe2a)
  }

}
