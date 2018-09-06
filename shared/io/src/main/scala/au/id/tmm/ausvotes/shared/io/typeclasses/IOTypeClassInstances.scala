package au.id.tmm.ausvotes.shared.io.typeclasses

import scalaz.zio.IO

object IOTypeClassInstances {

  implicit val ioIsAMonad: Monad[IO] = new Monad[IO] {
    override def pure[A](a: A): IO[Nothing, A] = IO.point(a)

    override def leftPure[E](e: E): IO[E, Nothing] = IO.fail(e)

    override def fromEither[E, A](either: Either[E, A]): IO[E, A] = IO.fromEither(either)

    override def flatten[E1, E2 >: E1, A](io: IO[E1, IO[E2, A]]): IO[E2, A] = io.flatMap(identity)

    override def flatMap[E1, E2 >: E1, A, B](io: IO[E1, A])(fafe2b: A => IO[E2, B]): IO[E2, B] = io.flatMap(fafe2b)

    override def map[E, A, B](io: IO[E, A])(fab: A => B): IO[E, B] = io.map(fab)

    override def leftMap[E1, E2, A](io: IO[E1, A])(fe1e2: E1 => E2): IO[E2, A] = io.leftMap(fe1e2)
  }

  implicit val ioAccessesEnvVars: AccessesEnvVars[IO] = new AccessesEnvVars[IO] {
    override def envVars: IO[Nothing, Map[String, String]] = IO.sync(sys.env)
  }

  implicit val ioHasSyncEffects: SyncEffects[IO] = new SyncEffects[IO] {
    override def sync[A](effect: => A): IO[Nothing, A] = IO.sync(effect)

    override def syncException[A](effect: => A): IO[Exception, A] = IO.syncException(effect)

    override def syncCatch[E, A](effect: => A)(f: PartialFunction[Throwable, E]): IO[E, A] = IO.syncCatch(effect)(f)
  }

  implicit val ioCanAttempt: Attempt[IO] = new Attempt[IO] {
    override def attempt[E, A](io: IO[E, A]): IO[Nothing, Either[E, A]] = io.attempt
  }

}
