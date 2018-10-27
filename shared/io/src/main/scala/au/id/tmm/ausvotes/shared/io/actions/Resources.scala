package au.id.tmm.ausvotes.shared.io.actions

import au.id.tmm.ausvotes.shared.io.typeclasses.Monad

abstract class Resources[F[+_, +_] : Monad] {
  def resource(name: String): F[Nothing, Option[String]]
}

object Resources {
  def asString[F[+_, +_] : Resources](name: String): F[Nothing, Option[String]] =
    implicitly[Resources[F]].resource(name)
}
