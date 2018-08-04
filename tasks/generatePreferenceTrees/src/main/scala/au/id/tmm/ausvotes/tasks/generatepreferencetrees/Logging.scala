package au.id.tmm.ausvotes.tasks.generatepreferencetrees

import au.id.tmm.ausvotes.core.logging.{LoggedEvent, Logger}
import scalaz.zio.IO

import scala.concurrent.duration.Duration

object Logging {

  private val logger: Logger = Logger(Main.getClass)

  implicit class IoOps[+E, +A](io: IO[E, A]) {
    def timedLog(eventId: String, kvPairs: (String, Any)*): IO[E, A] = {
      for {
        startTime <- IO.sync(System.nanoTime())
        resultPreLogging <- io.attempt
        endTime <- IO.sync(System.nanoTime())
        _ <- {
          val duration = Duration.fromNanos(endTime - startTime)

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

}
