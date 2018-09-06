package au.id.tmm.ausvotes.shared.io

import java.time.Duration

import au.id.tmm.ausvotes.shared.io.typeclasses.Attempt.AttemptOps
import au.id.tmm.ausvotes.shared.io.typeclasses.Functor.FunctorOps
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad.MonadOps
import au.id.tmm.ausvotes.shared.io.typeclasses.{Attempt, Monad, SyncEffects}
import au.id.tmm.utilities.logging.{LoggedEvent, Logger}
import org.slf4j.event.Level

object Slf4jLogging {

  implicit class LoggingOps[F[+_, +_] : SyncEffects : Monad : Attempt, +E, +A](io: F[E, A]) {
    def timedLog(eventId: String, kvPairs: (String, Any)*)(implicit logger: Logger): F[E, A] = {
      for {
        startTime <- SyncEffects.sync(System.nanoTime())
        resultPreLogging <- io.attempt
        endTime <- SyncEffects.sync(System.nanoTime())
        _ <- {
          val duration = Duration.ofNanos(endTime - startTime)

          val loggedEvent = LoggedEvent(eventId, ("duration" -> duration.toMillis) +: kvPairs: _*)

          resultPreLogging match {
            case Right(_) => {
              loggedEvent.markSuccessful()
              SyncEffects.sync(logger.info(loggedEvent))
            }
            case Left(e) => {
              e match {
                case t: Throwable => loggedEvent.exception = Some(t)
                case _ => Unit
              }
              loggedEvent.markFailed()
              SyncEffects.sync(logger.info(loggedEvent))
            }
          }

        }
        result <- Monad.fromEither(resultPreLogging)
      } yield result
    }
  }

  def logError[F[+_, +_] : SyncEffects](eventId: String, kvPairs: (String, Any)*)(implicit logger: Logger): F[Nothing, Unit] =
    log(Level.ERROR, eventId, kvPairs: _*)

  def logWarn[F[+_, +_] : SyncEffects](eventId: String, kvPairs: (String, Any)*)(implicit logger: Logger): F[Nothing, Unit] =
    log(Level.WARN, eventId, kvPairs: _*)

  def logInfo[F[+_, +_] : SyncEffects](eventId: String, kvPairs: (String, Any)*)(implicit logger: Logger): F[Nothing, Unit] =
    log(Level.INFO, eventId, kvPairs: _*)

  def logDebug[F[+_, +_] : SyncEffects](eventId: String, kvPairs: (String, Any)*)(implicit logger: Logger): F[Nothing, Unit] =
    log(Level.DEBUG, eventId, kvPairs: _*)

  def logTrace[F[+_, +_] : SyncEffects](eventId: String, kvPairs: (String, Any)*)(implicit logger: Logger): F[Nothing, Unit] =
    log(Level.TRACE, eventId, kvPairs: _*)

  def log[F[+_, +_] : SyncEffects](level: Level, eventId: String, kvPairs: (String, Any)*)(implicit logger: Logger): F[Nothing, Unit] = {
    val doLog: LoggedEvent => Unit = level match {
      case Level.ERROR => logger.info
      case Level.WARN => logger.warn
      case Level.INFO => logger.info
      case Level.DEBUG => logger.debug
      case Level.TRACE => logger.trace
    }

    val logEvent = LoggedEvent(eventId, kvPairs: _*)

    SyncEffects.sync(doLog(logEvent))
  }

}
