package au.id.tmm.ausvotes.shared.io.actions

import au.id.tmm.ausvotes.shared.io.typeclasses.Monad
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad.MonadOps

abstract class EnvVars[F[+_, +_] : Monad] {
  def envVars: F[Nothing, Map[String, String]]

  def envVar(key: String): F[Nothing, Option[String]] = envVars.map(_.get(key))
}

object EnvVars {
  def envVars[F[+_, +_] : EnvVars : Monad]: F[Nothing, Map[String, String]] =
    implicitly[EnvVars[F]].envVars

  def envVar[F[+_, +_] : EnvVars : Monad](key: String): F[Nothing, Option[String]] =
    implicitly[EnvVars[F]].envVar(key)
}
