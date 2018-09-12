package au.id.tmm.ausvotes.shared.io.actions

import java.time.{Instant, LocalDate, ZonedDateTime}

import au.id.tmm.ausvotes.shared.io.actions.Logging.LoggedEvent
import au.id.tmm.ausvotes.shared.io.typeclasses.IOTypeClassInstances._
import org.slf4j
import org.slf4j.{Logger, LoggerFactory}
import scalaz.zio.IO

object IOActions {

  object IOCurrentTime extends CurrentTime[IO] {
    override def systemNanoTime: IO[Nothing, Long] = IO.sync(System.nanoTime())
    override def currentTimeMillis: IO[Nothing, Long] = IO.sync(System.currentTimeMillis())
    override def nowInstant: IO[Nothing, Instant] = IO.sync(Instant.now())
    override def nowLocalDate: IO[Nothing, LocalDate] = IO.sync(LocalDate.now())
    override def nowZonedDateTime: IO[Nothing, ZonedDateTime] = IO.sync(ZonedDateTime.now())
  }

  object IOEnvVars extends EnvVars[IO] {
    override def envVars: IO[Nothing, Map[String, String]] = IO.sync(sys.env)
  }

  object IOSync extends Sync[IO] {
    override def sync[A](effect: => A): IO[Nothing, A] = IO.sync(effect)
    override def syncException[A](effect: => A): IO[Exception, A] = IO.syncException(effect)
    override def syncCatch[E, A](effect: => A)(f: PartialFunction[Throwable, E]): IO[E, A] = IO.syncCatch(effect)(f)
  }

  object IOLogging extends Logging[IO] {
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

}
