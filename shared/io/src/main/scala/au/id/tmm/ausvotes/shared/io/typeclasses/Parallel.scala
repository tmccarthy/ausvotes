package au.id.tmm.ausvotes.shared.io.typeclasses

abstract class Parallel[F[+_, +_]] {

  def par[E1, E2 >: E1, A, B](left: F[E1, A], right: F[E2, B]): F[E2, (A, B)]

  def parAll[E, A](as: Iterable[F[E, A]]): F[E, List[A]]

  def parTraverse[E, A, B](as: Iterable[A])(f: A => F[E, B]): F[E, List[B]]

}

object Parallel {

  implicit class ParallelOps[F[+_, +_] : Parallel, E1, A](left: F[E1, A]) {
    def par[E2 >: E1, B](right: F[E2, B]): F[E2, (A, B)] =
      implicitly[Parallel[F]].par(left, right)
  }

  def parAll[F[+_, +_] : Parallel, E, A](as: Iterable[F[E, A]]): F[E, List[A]] =
    implicitly[Parallel[F]].parAll(as)

  def parTraverse[F[+_, +_] : Parallel, E, A, B](as: Iterable[A])(f: A => F[E, B]): F[E, List[B]] =
    implicitly[Parallel[F]].parTraverse(as)(f)

}
