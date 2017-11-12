package au.id.tmm.ausvotes.core.logging

import java.util.Objects

import org.apache.commons.lang3.StringUtils
import org.slf4j

class Logger protected (val underlying: slf4j.Logger) {

  import Logger._

  @inline
  def error(eventId: String, kvPairs: (String, Any)*): Unit =
    if (underlying.isErrorEnabled) underlying.error(format(eventId, kvPairs))

  @inline
  def error(loggedEvent: LoggedEvent): Unit =
    if (underlying.isErrorEnabled) {
      val msg = format(loggedEvent.eventId, loggedEvent.kvPairs)
      if (loggedEvent.exception.isDefined) {
        underlying.error(msg, loggedEvent.exception.get)
      } else {
        underlying.error(msg)
      }
    }

  @inline
  def lazyEvalError(eventId: String, kvPairs: => Iterable[(String, Any)]): Unit =
    if (underlying.isErrorEnabled) underlying.error(format(eventId, kvPairs))

  @inline
  def warn(eventId: String, kvPairs: (String, Any)*): Unit =
    if (underlying.isWarnEnabled) underlying.warn(format(eventId, kvPairs))

  @inline
  def warn(loggedEvent: LoggedEvent): Unit =
    if (underlying.isWarnEnabled) {
      val msg = format(loggedEvent.eventId, loggedEvent.kvPairs)
      if (loggedEvent.exception.isDefined) {
        underlying.warn(msg, loggedEvent.exception.get)
      } else {
        underlying.warn(msg)
      }
    }

  @inline
  def lazyEvalWarn(eventId: String, kvPairs: => Iterable[(String, Any)]): Unit =
    if (underlying.isWarnEnabled) underlying.warn(format(eventId, kvPairs))

  @inline
  def info(eventId: String, kvPairs: (String, Any)*): Unit =
    if (underlying.isInfoEnabled) underlying.info(format(eventId, kvPairs))

  @inline
  def info(loggedEvent: LoggedEvent): Unit =
    if (underlying.isInfoEnabled) {
      val msg = format(loggedEvent.eventId, loggedEvent.kvPairs)
      if (loggedEvent.exception.isDefined) {
        underlying.info(msg, loggedEvent.exception.get)
      } else {
        underlying.info(msg)
      }
    }

  @inline
  def lazyEvalInfo(eventId: String, kvPairs: => Iterable[(String, Any)]): Unit =
    if (underlying.isInfoEnabled) underlying.info(format(eventId, kvPairs))

  @inline
  def debug(eventId: String, kvPairs: (String, Any)*): Unit =
    if (underlying.isDebugEnabled) underlying.debug(format(eventId, kvPairs))

  @inline
  def debug(loggedEvent: LoggedEvent): Unit =
    if (underlying.isDebugEnabled) {
      val msg = format(loggedEvent.eventId, loggedEvent.kvPairs)
      if (loggedEvent.exception.isDefined) {
        underlying.debug(msg, loggedEvent.exception.get)
      } else {
        underlying.debug(msg)
      }
    }

  @inline
  def lazyEvalDebug(eventId: String, kvPairs: => Iterable[(String, Any)]): Unit =
    if (underlying.isDebugEnabled) underlying.debug(format(eventId, kvPairs))

  @inline
  def trace(eventId: String, kvPairs: (String, Any)*): Unit =
    if (underlying.isTraceEnabled) underlying.trace(format(eventId, kvPairs))

  @inline
  def trace(loggedEvent: LoggedEvent): Unit =
    if (underlying.isTraceEnabled) {
      val msg = format(loggedEvent.eventId, loggedEvent.kvPairs)
      if (loggedEvent.exception.isDefined) {
        underlying.trace(msg, loggedEvent.exception.get)
      } else {
        underlying.trace(msg)
      }
    }

  @inline
  def lazyEvalTrace(eventId: String, kvPairs: => Iterable[(String, Any)]): Unit =
    if (underlying.isTraceEnabled) underlying.trace(format(eventId, kvPairs))

}

object Logger {

  def apply(name: String): Logger = {
    val className = name.stripSuffix("$")

    val underlying = slf4j.LoggerFactory.getLogger(className)

    new Logger(underlying)
  }

  def apply(cls: Class[_]): Logger = apply(cls.getName)

  def apply(): Logger = {
    val stackTrace = Thread.currentThread().getStackTrace

    val callingClassName = stackTrace
      .toStream
      .filterNot(_.getClassName == classOf[Thread].getName)
      .filterNot(_.getClassName == getClass.getName)
      .head
      .getClassName

    Logger(callingClassName)
  }

  private[logging] def format(eventId: String,
                              kvPairs: Iterable[(String, Any)],
                             ): String = {
    val logMessage = new StringBuilder

    logMessage.append("event_id=").append(escapeForFormat(eventId))

    for ((key, valueObject) <- kvPairs) {
      val valueString = Objects.toString(valueObject, null)

      logMessage.append(separatorChar).append(" ")

      logMessage.append(key).append("=")

      logMessage.append(escapeForFormat(valueString))
    }

    logMessage.toString
  }

  private val nullRepresentation = "<null>"

  private val quoteChar = '"'
  private val separatorChar = ';'
  private val equalityChar = '='
  private val escapeChar = '\\'

  private val searchChars = Array(quoteChar, separatorChar, equalityChar, escapeChar)

  private val searchStrings = searchChars.map(String.valueOf)
  private val replacements = searchStrings.map("" + escapeChar + _)

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