package au.id.tmm.ausvotes.shared.io

import java.time.Duration

import au.id.tmm.ausvotes.shared.io.typeclasses.Attempt.AttemptOps
import au.id.tmm.ausvotes.shared.io.typeclasses.Functor.FunctorOps
import au.id.tmm.ausvotes.shared.io.typeclasses.Log.LoggedEvent
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad.MonadOps
import au.id.tmm.ausvotes.shared.io.typeclasses._

object Logging {

  // TODO figure out how to get rid of this duplication

  implicit class LoggingOps[F[+_, +_] : Log : Now : Monad : Attempt, +E, +A](fea: F[E, A]) {
    def timedLog(eventId: String, kvPairs: (String, Any)*): F[E, A] =
      for {
        startTime <- Now.systemNanoTime
        resultPreLogging <- fea.attempt
        endTime <- Now.systemNanoTime
        duration = Duration.ofNanos(endTime - startTime).toMillis
        _ <- doLog(eventId, kvPairs.toList, duration)(resultPreLogging)
        result <- Monad.fromEither(resultPreLogging)
      } yield result
  }

  def timedLog[F[+_, +_] : Log : Now : Monad, E, A](eventId: String, kvPairs: (String, Any)*)(action: => Either[E, A]): F[E, A] = {
    for {
      startTime <- Now.systemNanoTime
      resultPreLogging = action
      endTime <- Now.systemNanoTime
      duration = Duration.ofNanos(endTime - startTime).toMillis
      _ <- doLog(eventId, kvPairs.toList, duration)(resultPreLogging)
      result <- Monad.fromEither(resultPreLogging)
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
