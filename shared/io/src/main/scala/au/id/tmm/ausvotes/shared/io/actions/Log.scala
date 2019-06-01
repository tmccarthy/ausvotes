package au.id.tmm.ausvotes.shared.io.actions

import java.util.Objects

import au.id.tmm.ausvotes.shared.io.actions.Log.LoggedEvent
import org.apache.commons.lang3.StringUtils
import org.slf4j
import org.slf4j.LoggerFactory
import scalaz.zio.IO

trait Log[F[+_, +_]] {

  def logError(loggedEvent: LoggedEvent): F[Nothing, Unit]
  def logWarn(loggedEvent: LoggedEvent): F[Nothing, Unit]
  def logInfo(loggedEvent: LoggedEvent): F[Nothing, Unit]
  def logDebug(loggedEvent: LoggedEvent): F[Nothing, Unit]
  def logTrace(loggedEvent: LoggedEvent): F[Nothing, Unit]

  def log(level: Log.Level, event: LoggedEvent): F[Nothing, Unit] = level match {
    case Log.Level.Error => logError(event)
    case Log.Level.Warn => logWarn(event)
    case Log.Level.Info => logInfo(event)
    case Log.Level.Debug => logDebug(event)
    case Log.Level.Trace => logTrace(event)
  }

}

object Log {

  def logError[F[+_, +_] : Log](loggedEvent: LoggedEvent): F[Nothing, Unit] = implicitly[Log[F]].logError(loggedEvent)
  def logWarn[F[+_, +_] : Log](loggedEvent: LoggedEvent): F[Nothing, Unit] = implicitly[Log[F]].logWarn(loggedEvent)
  def logInfo[F[+_, +_] : Log](loggedEvent: LoggedEvent): F[Nothing, Unit] = implicitly[Log[F]].logInfo(loggedEvent)
  def logDebug[F[+_, +_] : Log](loggedEvent: LoggedEvent): F[Nothing, Unit] = implicitly[Log[F]].logDebug(loggedEvent)
  def logTrace[F[+_, +_] : Log](loggedEvent: LoggedEvent): F[Nothing, Unit] = implicitly[Log[F]].logTrace(loggedEvent)

  def logError[F[+_, +_] : Log](id: String, kvPairs: (String, Any)*): F[Nothing, Unit] =
    implicitly[Log[F]].logError(LoggedEvent(id, kvPairs.toList, exception = None))

  def logWarn[F[+_, +_] : Log](id: String, kvPairs: (String, Any)*): F[Nothing, Unit] =
    implicitly[Log[F]].logWarn(LoggedEvent(id, kvPairs.toList, exception = None))

  def logInfo[F[+_, +_] : Log](id: String, kvPairs: (String, Any)*): F[Nothing, Unit] =
    implicitly[Log[F]].logInfo(LoggedEvent(id, kvPairs.toList, exception = None))

  def logDebug[F[+_, +_] : Log](id: String, kvPairs: (String, Any)*): F[Nothing, Unit] =
    implicitly[Log[F]].logDebug(LoggedEvent(id, kvPairs.toList, exception = None))

  def logTrace[F[+_, +_] : Log](id: String, kvPairs: (String, Any)*): F[Nothing, Unit] =
    implicitly[Log[F]].logTrace(LoggedEvent(id, kvPairs.toList, exception = None))

  final case class LoggedEvent(id: String, kvPairs: List[(String, Any)], exception: Option[Throwable])

  object LoggedEvent {
    private val nullRepresentation = "<null>"

    private val quoteChar = '"'
    private val separatorChar = ';'
    private val equalityChar = '='
    private val escapeChar = '\\'

    private val searchChars = Array(quoteChar, separatorChar, equalityChar, escapeChar)

    private val searchStrings: Array[String] = searchChars.map(String.valueOf)
    private val replacements = searchStrings.map("" + escapeChar + _)

    // TODO shift this somewhere common
    def formatMessage(loggedEvent: LoggedEvent): String = {
      val logMessage = new StringBuilder

      logMessage.append("event_id=").append(escapeForFormat(loggedEvent.id))

      for ((key, valueObject) <- loggedEvent.kvPairs) {
        val valueString = Objects.toString(valueObject, null)

        logMessage.append(separatorChar).append(" ")

        logMessage.append(key).append("=")

        logMessage.append(escapeForFormat(valueString))
      }

      logMessage.toString
    }

    private def escapeForFormat(value: String): String = {
      if (value eq null) {
        nullRepresentation
      } else if (StringUtils.containsAny(value, searchChars) || value == nullRepresentation || StringUtils.containsWhitespace(value)) {
        val output = new StringBuilder

        output.append(quoteChar)
        output.append(StringUtils.replaceEach(value, searchStrings, replacements))
        output.append(quoteChar)

        output.toString()
      } else {
        value
      }
    }
  }

  sealed trait Level

  object Level {
    case object Error extends Level
    case object Warn extends Level
    case object Info extends Level
    case object Debug extends Level
    case object Trace extends Level
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

}
