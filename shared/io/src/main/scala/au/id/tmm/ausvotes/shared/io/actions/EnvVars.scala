package au.id.tmm.ausvotes.shared.io.actions

import au.id.tmm.ausvotes.shared.io.typeclasses.Functor
import au.id.tmm.ausvotes.shared.io.typeclasses.Functor.FunctorOps

abstract class EnvVars[F[+_, +_] : Functor] {
  def envVars: F[Nothing, Map[String, String]]

  def envVar(key: String): F[Nothing, Option[String]] = envVars.map(_.get(key))
}

object EnvVars {
  def envVars[F[+_, +_] : EnvVars : Functor]: F[Nothing, Map[String, String]] =
    implicitly[EnvVars[F]].envVars

  def envVar[F[+_, +_] : EnvVars : Functor](key: String): F[Nothing, Option[String]] =
    implicitly[EnvVars[F]].envVar(key)
}
