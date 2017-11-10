package au.id.tmm.ausvotes.core.logging

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

final class LoggedEvent private (val eventId: String,
                                 val kvPairs: mutable.ArrayBuffer[(String, Any)] = mutable.ArrayBuffer[(String, Any)](),
                                 var exception: Option[Throwable] = None,
                                ) {
  def markSuccessful(): Unit = kvPairs.+=:("successful" -> true)

  def markFailed(): Unit = kvPairs.+=:("successful" -> false)

  def logWithTimeOnceFinished[A](block: => A)(implicit logger: Logger): A = {
    this.logOnceFinished {
      val start = System.currentTimeMillis()
      val result = block
      val duration = System.currentTimeMillis() - start

      kvPairs.+=:("duration" -> duration)

      result
    }
  }

  def logOnceFinished[A](block: => A)(implicit logger: Logger): A = {
    try {
      val result = block

      markSuccessful()
      logger.info(this)

      result
    } catch {
      case e: Throwable => {
        this.exception = Some(e)
        markFailed()
        logger.error(this)

        throw e
      }
    }
  }
}

object LoggedEvent {
  def apply(eventId: String) = new LoggedEvent(eventId)

  def apply(eventId: String, kvPairs: (String, Any)*) = new LoggedEvent(eventId, kvPairs = ArrayBuffer(kvPairs :_*))

  implicit class TryOps[A](aTry: Try[A]) {
    def logEvent(eventId: String, kvPairs: (String, Any)*)(implicit logger: Logger): Try[A] = {
      val loggedEvent = LoggedEvent(eventId, kvPairs: _*)

      aTry match {
        case Success(_) => {
          loggedEvent.markSuccessful()
          logger.info(loggedEvent)
        }
        case Failure(e) => {
          loggedEvent.markFailed()
          loggedEvent.exception = Some(e)
          logger.error(loggedEvent)
        }
      }

      aTry
    }
  }

  implicit class FutureOps[A](future: Future[A]) {
    def logEvent(eventId: String, kvPairs: (String, Any)*)(implicit logger: Logger, ec: ExecutionContext): Future[A] = {
      future.andThen {
        case t: Try[A] => t.logEvent(eventId, kvPairs: _*)
      }
    }
  }
}