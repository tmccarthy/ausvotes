package au.id.tmm.ausvotes.shared.io.typeclasses

import au.id.tmm.ausvotes.shared.io.typeclasses.Functor.FunctorOps

abstract class AccessesEnvVars[F[+_, +_] : Functor] {
  def envVars: F[Nothing, Map[String, String]]

  def envVar(key: String): F[Nothing, Option[String]] = envVars.map(_.get(key))
}

object AccessesEnvVars {
  def envVars[F[+_, +_] : AccessesEnvVars : Functor]: F[Nothing, Map[String, String]] =
    implicitly[AccessesEnvVars[F]].envVars

  def envVar[F[+_, +_] : AccessesEnvVars : Functor](key: String): F[Nothing, Option[String]] =
    implicitly[AccessesEnvVars[F]].envVar(key)
}
