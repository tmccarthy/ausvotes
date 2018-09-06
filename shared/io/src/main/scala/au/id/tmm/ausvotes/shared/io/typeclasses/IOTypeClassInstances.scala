package au.id.tmm.ausvotes.shared.io.typeclasses

import scalaz.zio.IO

object IOTypeClassInstances {

  implicit val ioIsAMonad: Monad[IO] = new Monad[IO] {
    override def pure[A](a: A): IO[Nothing, A] = IO.point(a)

    override def leftPure[E](e: E): IO[E, Nothing] = IO.fail(e)

    override def flatten[E1, E2 <: E1, A](io: IO[E1, IO[E2, A]]): IO[E1, A] = io.flatMap(identity)

    override def flatMap[E1, E2 <: E1, A, B](io: IO[E1, A])(fafe2b: A => IO[E2, B]): IO[E1, B] = io.flatMap(fafe2b)

    override def map[E, A, B](io: IO[E, A])(fab: A => B): IO[E, B] = io.map(fab)

    override def leftMap[E1, E2, A](io: IO[E1, A])(fe1e2: E1 => E2): IO[E2, A] = io.leftMap(fe1e2)
  }

  implicit val ioAccessesEnvVars: AccessesEnvVars[IO] = new AccessesEnvVars[IO] {
    override def envVars: IO[Nothing, Map[String, String]] = IO.sync(sys.env)
  }

}
