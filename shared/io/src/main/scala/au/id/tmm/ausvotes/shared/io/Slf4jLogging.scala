package au.id.tmm.ausvotes.shared.io

import java.time.Duration

import au.id.tmm.utilities.logging.{LoggedEvent, Logger}
import org.slf4j.event.Level
import scalaz.zio.IO

object Slf4jLogging {

  implicit class IoOps[+E, +A](io: IO[E, A]) {

    def timedLog(eventId: String, kvPairs: (String, Any)*)(implicit logger: Logger): IO[E, A] = {
      for {
        startTime <- IO.sync(System.nanoTime())
        resultPreLogging <- io.attempt
        endTime <- IO.sync(System.nanoTime())
        _ <- {
          val duration = Duration.ofNanos(endTime - startTime)

          val loggedEvent = LoggedEvent(eventId, ("duration" -> duration.toMillis) +: kvPairs: _*)

          resultPreLogging match {
            case Right(_) => {
              loggedEvent.markSuccessful()
              IO.sync(logger.info(loggedEvent))
            }
            case Left(e) => {
              e match {
                case t: Throwable => loggedEvent.exception = Some(t)
              }
              loggedEvent.markFailed()
              IO.sync(logger.info(loggedEvent))
            }
          }

        }
        result <- IO.fromEither(resultPreLogging)
      } yield result
    }
  }

  def logError(eventId: String, kvPairs: (String, Any)*)(implicit logger: Logger): IO[Nothing, Unit] =
    log(Level.ERROR, eventId, kvPairs: _*)

  def logWarn(eventId: String, kvPairs: (String, Any)*)(implicit logger: Logger): IO[Nothing, Unit] =
    log(Level.WARN, eventId, kvPairs: _*)

  def logInfo(eventId: String, kvPairs: (String, Any)*)(implicit logger: Logger): IO[Nothing, Unit] =
    log(Level.INFO, eventId, kvPairs: _*)

  def logDebug(eventId: String, kvPairs: (String, Any)*)(implicit logger: Logger): IO[Nothing, Unit] =
    log(Level.DEBUG, eventId, kvPairs: _*)

  def logTrace(eventId: String, kvPairs: (String, Any)*)(implicit logger: Logger): IO[Nothing, Unit] =
    log(Level.TRACE, eventId, kvPairs: _*)

  def log(level: Level, eventId: String, kvPairs: (String, Any)*)(implicit logger: Logger): IO[Nothing, Unit] = {
    val doLog: LoggedEvent => Unit = level match {
      case Level.ERROR => logger.info
      case Level.WARN => logger.warn
      case Level.INFO => logger.info
      case Level.DEBUG => logger.debug
      case Level.TRACE => logger.trace
    }

    val logEvent = LoggedEvent(eventId, kvPairs: _*)

    IO.sync(doLog(logEvent))
  }

}
