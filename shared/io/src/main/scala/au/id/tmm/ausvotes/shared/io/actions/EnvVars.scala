package au.id.tmm.ausvotes.shared.io.actions

import au.id.tmm.ausvotes.shared.io.typeclasses.Monad
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad.MonadOps

trait EnvVars[F[+_, +_]] {
  def envVars: F[Nothing, Map[String, String]]
}

object EnvVars {
  def envVars[F[+_, +_] : EnvVars : Monad]: F[Nothing, Map[String, String]] =
    implicitly[EnvVars[F]].envVars

  def envVar[F[+_, +_] : EnvVars : Monad](key: String): F[Nothing, Option[String]] =
    implicitly[EnvVars[F]].envVars.map(_.get(key))

  def envVarOr[F[+_, +_] : EnvVars : Monad, E](key: String, onMissing: => E): F[E, String] =
    envVar[F](key).flatMap {
      case Some(value) => Monad.pure[F, String](value)
      case None => Monad.leftPure[F, E](onMissing)
    }
}
