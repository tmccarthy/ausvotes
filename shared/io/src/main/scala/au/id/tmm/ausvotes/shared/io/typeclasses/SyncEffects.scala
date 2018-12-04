package au.id.tmm.ausvotes.shared.io.typeclasses

trait SyncEffects[F[_, +_]] {
  def sync[A](effect: => A): F[Nothing, A]
  def syncException[A](effect: => A): F[Exception, A]
  def syncCatch[E, A](effect: => A)(f: PartialFunction[Throwable, E]): F[E, A]
}

object SyncEffects {
  def sync[F[+_, +_] : SyncEffects, A](effect: => A): F[Nothing, A] =
    implicitly[SyncEffects[F]].sync(effect)
  def syncException[F[+_, +_] : SyncEffects, A](effect: => A): F[Exception, A] =
    implicitly[SyncEffects[F]].syncException(effect)
  def syncCatch[F[+_, +_] : SyncEffects, E, A](effect: => A)(f: PartialFunction[Throwable, E]): F[E, A] =
    implicitly[SyncEffects[F]].syncCatch(effect)(f)
}
