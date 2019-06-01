package au.id.tmm.ausvotes.shared.io.test.testdata

import au.id.tmm.ausvotes.shared.io.actions.Log
import au.id.tmm.ausvotes.shared.io.actions.Log.LoggedEvent
import au.id.tmm.bfect.testing.BState

final case class LoggingTestData(
                                  loggedMessages: Map[Log.Level, List[LoggedEvent]],
                                ) {
  def log(level: Log.Level, event: LoggedEvent): LoggingTestData =
    this.copy(
      loggedMessages = this.loggedMessages.updated(level, this.loggedMessages.getOrElse(level, Nil) :+ event)
    )
}

object LoggingTestData {

  val empty = LoggingTestData(Map.empty)

  trait TestIOInstance[D] extends Log[BState[D, +?, +?]] {
    protected def loggingTestDataField(data: D): LoggingTestData
    protected def setLoggingTestData(oldData: D, newLoggingTestData: LoggingTestData): D

    override def logError(loggedEvent: LoggedEvent): BState[D, Nothing, Unit] = log(Log.Level.Error, loggedEvent)
    override def logWarn(loggedEvent: LoggedEvent): BState[D, Nothing, Unit] = log(Log.Level.Warn, loggedEvent)
    override def logInfo(loggedEvent: LoggedEvent): BState[D, Nothing, Unit] = log(Log.Level.Info, loggedEvent)
    override def logDebug(loggedEvent: LoggedEvent): BState[D, Nothing, Unit] = log(Log.Level.Debug, loggedEvent)
    override def logTrace(loggedEvent: LoggedEvent): BState[D, Nothing, Unit] = log(Log.Level.Trace, loggedEvent)

    override def log(level: Log.Level, event: Log.LoggedEvent): BState[D, Nothing, Unit] =
      BState(oldData => {
        val oldLoggingTestData = loggingTestDataField(oldData)

        val newLoggingTestData = oldLoggingTestData.log(level, event)

        val newData = setLoggingTestData(oldData, newLoggingTestData)

        (newData, Right(()))
      })
  }

}
