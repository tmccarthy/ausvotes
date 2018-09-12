package au.id.tmm.ausvotes.shared.io.actions

import java.util.Objects

import au.id.tmm.ausvotes.shared.io.actions.Logging.LoggedEvent
import org.apache.commons.lang3.StringUtils

abstract class Logging[F[+_, +_]] {

  def logError(loggedEvent: LoggedEvent): F[Nothing, Unit]
  def logWarn(loggedEvent: LoggedEvent): F[Nothing, Unit]
  def logInfo(loggedEvent: LoggedEvent): F[Nothing, Unit]
  def logDebug(loggedEvent: LoggedEvent): F[Nothing, Unit]
  def logTrace(loggedEvent: LoggedEvent): F[Nothing, Unit]

  def log(level: Logging.Level, event: LoggedEvent): F[Nothing, Unit] = level match {
    case Logging.Level.Error => logError(event)
    case Logging.Level.Warn => logWarn(event)
    case Logging.Level.Info => logInfo(event)
    case Logging.Level.Debug => logDebug(event)
    case Logging.Level.Trace => logTrace(event)
  }

}

object Logging {

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

}
