package au.id.tmm.ausvotes.shared.io.typeclasses

abstract class Functor[F[+_, +_]] {
  def map[E, A, B](fea: F[E, A])(fab: A => B): F[E, B]
  def leftMap[E1, E2, A](fea: F[E1, A])(fe1e2: E1 => E2): F[E2, A]
}

object Functor {
  implicit class FunctorOps[F[+_, +_] : Functor, E, A](fea: F[E, A]) {
    def map[B](f: A => B): F[E, B] = implicitly[Functor[F]].map(fea)(f)
    def leftMap[E2](f: E => E2): F[E2, A] = implicitly[Functor[F]].leftMap(fea)(f)
  }
}