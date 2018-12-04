package au.id.tmm.ausvotes.shared.io.actions

import au.id.tmm.ausvotes.shared.io.typeclasses.BifunctorMonadError.Ops
import au.id.tmm.ausvotes.shared.io.typeclasses.{BifunctorMonadError => BME}

trait EnvVars[F[+_, +_]] {
  def envVars: F[Nothing, Map[String, String]]
}

object EnvVars {
  def envVars[F[+_, +_] : EnvVars : BME]: F[Nothing, Map[String, String]] =
    implicitly[EnvVars[F]].envVars

  def envVar[F[+_, +_] : EnvVars : BME](key: String): F[Nothing, Option[String]] =
    implicitly[EnvVars[F]].envVars.map(_.get(key))

  def envVarOr[F[+_, +_] : EnvVars : BME, E](key: String, onMissing: => E): F[E, String] =
    envVar[F](key).flatMap {
      case Some(value) => BME.pure[F, String](value): F[E, String]
      case None => BME.leftPure[F, E](onMissing): F[E, String]
    }
}
