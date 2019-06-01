package au.id.tmm.ausvotes.shared.io

import java.time.Duration

import au.id.tmm.ausvotes.shared.io.actions.Log.LoggedEvent
import au.id.tmm.ausvotes.shared.io.actions.Log
import au.id.tmm.bfect.BME
import au.id.tmm.bfect.BME._
import au.id.tmm.bfect.effects.Now

object Logging {

  // TODO figure out how to get rid of this duplication

  implicit class LoggingOps[F[+_, +_] : Log : Now : BME, +E, +A](fea: F[E, A]) {
    def timedLog(eventId: String, kvPairs: (String, Any)*): F[E, A] =
      for {
        startTime <- Now.now
        resultPreLogging <- fea.attempt
        endTime <- Now.now
        duration = Duration.between(startTime, endTime).toMillis
        _ <- doLog(eventId, kvPairs.toList, duration)(resultPreLogging)
        result <- BME.fromEither(resultPreLogging)
      } yield result
  }

  def timedLog[F[+_, +_] : Log : Now : BME, E, A](eventId: String, kvPairs: (String, Any)*)(action: => Either[E, A]): F[E, A] = {
    for {
      startTime <- Now.now
      resultPreLogging = action
      endTime <- Now.now
      duration = Duration.between(startTime, endTime).toMillis
      _ <- doLog(eventId, kvPairs.toList, duration)(resultPreLogging)
      result <- BME.fromEither(resultPreLogging)
    } yield result
  }

  private def doLog[F[+_, +_] : Log, E, A](eventId: String, kvPairs: List[(String, Any)], duration: Long)(result: Either[E, A]): F[Nothing, Unit] = result match {
    case Right(_) => Log.logInfo(
      LoggedEvent(
        eventId,
        kvPairs ++ List("successful" -> true, "duration" -> duration),
        exception = None,
      )
    )
    case Left(e) => Log.logError(
      LoggedEvent(
        eventId,
        kvPairs ++ List("successful" -> false, "duration" -> duration),
        exception = e match {
          case t: Throwable => Some(t)
          case _ => None
        },
      )
    )
  }
}
