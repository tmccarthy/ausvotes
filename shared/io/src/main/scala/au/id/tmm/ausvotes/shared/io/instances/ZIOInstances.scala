package au.id.tmm.ausvotes.shared.io.instances

import java.io.IOException
import java.nio.charset.Charset
import java.time.{Instant, LocalDate, ZonedDateTime}

import au.id.tmm.ausvotes.shared.io.actions.Log.LoggedEvent
import au.id.tmm.ausvotes.shared.io.actions._
import au.id.tmm.bfect.extraeffects.{EnvVars, Resources}
import org.apache.commons.io.IOUtils
import org.slf4j
import org.slf4j.LoggerFactory
import scalaz.zio.IO

object ZIOInstances {

  implicit val ioAccessesEnvVars: EnvVars[IO] = new EnvVars[IO] {
    override def envVars: IO[Nothing, Map[String, String]] = IO.effectTotal(sys.env)
  }

  implicit val ioAccessesResources: Resources[IO] = new Resources[IO] {
    override def resourceAsString(resourceName: String, charset: Charset): IO[IOException, Option[String]] = IO.effectTotal {
      Option(IOUtils.toString(getClass.getResource(resourceName), "UTF-8"))
    }
  }

  implicit val ioCanLog: Log[IO] = new Log[IO] {
    private val logger: slf4j.Logger = LoggerFactory.getLogger("au.id.tmm.ausvotes")

    override def logError(loggedEvent: LoggedEvent): IO[Nothing, Unit] =
      IO.effectTotal(logger.error(LoggedEvent.formatMessage(loggedEvent), loggedEvent.exception.orNull))

    override def logWarn(loggedEvent: LoggedEvent): IO[Nothing, Unit] =
      IO.effectTotal(logger.warn(LoggedEvent.formatMessage(loggedEvent), loggedEvent.exception.orNull))

    override def logInfo(loggedEvent: LoggedEvent): IO[Nothing, Unit] =
      IO.effectTotal(logger.info(LoggedEvent.formatMessage(loggedEvent), loggedEvent.exception.orNull))

    override def logDebug(loggedEvent: LoggedEvent): IO[Nothing, Unit] =
      IO.effectTotal(logger.debug(LoggedEvent.formatMessage(loggedEvent), loggedEvent.exception.orNull))

    override def logTrace(loggedEvent: LoggedEvent): IO[Nothing, Unit] =
      IO.effectTotal(logger.trace(LoggedEvent.formatMessage(loggedEvent), loggedEvent.exception.orNull))

  }

  implicit val ioCanDetermineNow: Now[IO] = new Now[IO] {
    override def systemNanoTime: IO[Nothing, Long] = IO.effectTotal(System.nanoTime())

    override def currentTimeMillis: IO[Nothing, Long] = IO.effectTotal(System.currentTimeMillis())

    override def nowInstant: IO[Nothing, Instant] = IO.effectTotal(Instant.now())

    override def nowLocalDate: IO[Nothing, LocalDate] = IO.effectTotal(LocalDate.now())

    override def nowZonedDateTime: IO[Nothing, ZonedDateTime] = IO.effectTotal(ZonedDateTime.now())
  }

  implicit val zioHasAConsole: Console[IO] = new Console[IO] {
    override def print(string: String): IO[Nothing, Unit] = IO.effectTotal(scala.Console.print(string))

    override def println(string: String): IO[Nothing, Unit] = IO.effectTotal(scala.Console.println(string))
  }

}
