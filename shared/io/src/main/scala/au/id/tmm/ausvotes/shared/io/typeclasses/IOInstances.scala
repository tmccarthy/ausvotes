package au.id.tmm.ausvotes.shared.io.typeclasses

import java.time.{Instant, LocalDate, ZonedDateTime}

import au.id.tmm.ausvotes.shared.io.actions.Log.LoggedEvent
import au.id.tmm.ausvotes.shared.io.actions.{EnvVars, Log, Now}
import org.slf4j
import org.slf4j.{Logger, LoggerFactory}
import scalaz.zio.IO

object IOInstances {

  implicit val ioIsAMonad: Monad[IO] = new Monad[IO] {
    override def pure[A](a: A): IO[Nothing, A] = IO.point(a)

    override def leftPure[E](e: E): IO[E, Nothing] = IO.fail(e)

    override def fromEither[E, A](either: Either[E, A]): IO[E, A] = IO.fromEither(either)

    override def flatten[E1, E2 >: E1, A](io: IO[E1, IO[E2, A]]): IO[E2, A] = io.flatMap[E2, A](identity)

    override def flatMap[E1, E2 >: E1, A, B](io: IO[E1, A])(fafe2b: A => IO[E2, B]): IO[E2, B] = io.flatMap(fafe2b)

    override def map[E, A, B](io: IO[E, A])(fab: A => B): IO[E, B] = io.map(fab)

    override def leftMap[E1, E2, A](io: IO[E1, A])(fe1e2: E1 => E2): IO[E2, A] = io.leftMap(fe1e2)
  }

  implicit val ioAccessesEnvVars: EnvVars[IO] = new EnvVars[IO] {
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

  implicit val ioCanLog: Log[IO] = new Log[IO] {
    private val logger: slf4j.Logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)

    override def logError(loggedEvent: LoggedEvent): IO[Nothing, Unit] =
      IO.sync(logger.error(LoggedEvent.formatMessage(loggedEvent), loggedEvent.exception.orNull))

    override def logWarn(loggedEvent: LoggedEvent): IO[Nothing, Unit] =
      IO.sync(logger.warn(LoggedEvent.formatMessage(loggedEvent), loggedEvent.exception.orNull))

    override def logInfo(loggedEvent: LoggedEvent): IO[Nothing, Unit] =
      IO.sync(logger.info(LoggedEvent.formatMessage(loggedEvent), loggedEvent.exception.orNull))

    override def logDebug(loggedEvent: LoggedEvent): IO[Nothing, Unit] =
      IO.sync(logger.debug(LoggedEvent.formatMessage(loggedEvent), loggedEvent.exception.orNull))

    override def logTrace(loggedEvent: LoggedEvent): IO[Nothing, Unit] =
      IO.sync(logger.trace(LoggedEvent.formatMessage(loggedEvent), loggedEvent.exception.orNull))

  }

  implicit val ioCanDetermineNow: Now[IO] = new Now[IO] {
    override def systemNanoTime: IO[Nothing, Long] = IO.sync(System.nanoTime())

    override def currentTimeMillis: IO[Nothing, Long] = IO.sync(System.currentTimeMillis())

    override def nowInstant: IO[Nothing, Instant] = IO.sync(Instant.now())

    override def nowLocalDate: IO[Nothing, LocalDate] = IO.sync(LocalDate.now())

    override def nowZonedDateTime: IO[Nothing, ZonedDateTime] = IO.sync(ZonedDateTime.now())
  }

}
