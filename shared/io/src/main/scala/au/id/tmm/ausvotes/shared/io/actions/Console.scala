package au.id.tmm.ausvotes.shared.io.actions

// TODO move this to bfect
trait Console[F[+_, +_]] {

  def print(string: String): F[Nothing, Unit]
  def println(string: String): F[Nothing, Unit]

}

object Console {

  def print[F[+_, +_] : Console](string: String): F[Nothing, Unit] = implicitly[Console[F]].print(string)
  def println[F[+_, +_] : Console](string: String): F[Nothing, Unit] = implicitly[Console[F]].println(string)

}
