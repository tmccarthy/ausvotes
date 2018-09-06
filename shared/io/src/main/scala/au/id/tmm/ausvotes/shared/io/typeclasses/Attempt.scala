package au.id.tmm.ausvotes.shared.io.typeclasses

abstract class Attempt[F[+_, +_]] {
  def attempt[E, A](fea: F[E, A]): F[Nothing, Either[E, A]]
}

object Attempt {
  implicit class AttemptOps[F[+_, +_] : Attempt, E, A](fea: F[E, A]) {
    def attempt: F[Nothing, Either[E, A]] = implicitly[Attempt[F]].attempt(fea)
  }
}
