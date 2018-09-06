package au.id.tmm.ausvotes.shared.io.typeclasses

abstract class Monad[F[+_, +_]] extends Functor[F] {
  def pure[A](a: A): F[Nothing, A]
  def leftPure[E](e: E): F[E, Nothing]

  def flatten[E1, E2 <: E1, A](fefa: F[E1, F[E2, A]]): F[E1, A]

  def flatMap[E1, E2 <: E1, A, B](fe1a: F[E1, A])(fafe2b: A => F[E2, B]): F[E1, B]
}

object Monad {

  def pure[F[+_, +_] : Monad, A](a: A): F[Nothing, A] = implicitly[Monad[F]].pure(a)
  def leftPure[F[+_, +_] : Monad, E](e: E): F[E, Nothing] = implicitly[Monad[F]].leftPure(e)

  implicit class MonadOps[F[+_, +_] : Monad, E, A](fea: F[E, A]) {
    def flatMap[E2 <: E, B](fafe2b: A => F[E2, B]): F[E, B] = implicitly[Monad[F]].flatMap(fea)(fafe2b)
  }

  implicit class MonadFlattenOps[F[+_, +_] : Monad, E1, E2 <: E1, A](fe1fe2a: F[E1, F[E2, A]]) {
    def flatten: F[E1, A] = implicitly[Monad[F]].flatten(fe1fe2a)
  }

}
