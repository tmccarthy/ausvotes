package au.id.tmm.ausvotes.shared.io.actions

trait Resources[F[+_, +_]] {
  def resource(name: String): F[Nothing, Option[String]]
}

object Resources {
  def asString[F[+_, +_] : Resources](name: String): F[Nothing, Option[String]] =
    implicitly[Resources[F]].resource(name)
}
