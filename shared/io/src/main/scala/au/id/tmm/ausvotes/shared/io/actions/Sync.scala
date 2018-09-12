package au.id.tmm.ausvotes.shared.io.actions

abstract class Sync[F[+_, +_]] {
  def sync[A](effect: => A): F[Nothing, A]
  def syncException[A](effect: => A): F[Exception, A]
  def syncCatch[E, A](effect: => A)(f: PartialFunction[Throwable, E]): F[E, A]
}
