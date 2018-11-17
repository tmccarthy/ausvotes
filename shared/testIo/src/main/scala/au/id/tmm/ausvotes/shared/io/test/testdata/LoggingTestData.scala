package au.id.tmm.ausvotes.shared.io.test.testdata

import au.id.tmm.ausvotes.shared.io.actions.Log
import au.id.tmm.ausvotes.shared.io.actions.Log.LoggedEvent
import au.id.tmm.ausvotes.shared.io.test.TestIO
import au.id.tmm.ausvotes.shared.io.test.TestIO.Output

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

  def testIOInstance[D](
                         loggingTestDataField: D => LoggingTestData,
                         setLoggingTestData: (D, LoggingTestData) => D,
                       ): Log[TestIO[D, +?, +?]] = new Log[TestIO[D, +?, +?]] {

    override def logError(loggedEvent: LoggedEvent): TestIO[D, Nothing, Unit] = log(Log.Level.Error, loggedEvent)
    override def logWarn(loggedEvent: LoggedEvent): TestIO[D, Nothing, Unit] = log(Log.Level.Warn, loggedEvent)
    override def logInfo(loggedEvent: LoggedEvent): TestIO[D, Nothing, Unit] = log(Log.Level.Info, loggedEvent)
    override def logDebug(loggedEvent: LoggedEvent): TestIO[D, Nothing, Unit] = log(Log.Level.Debug, loggedEvent)
    override def logTrace(loggedEvent: LoggedEvent): TestIO[D, Nothing, Unit] = log(Log.Level.Trace, loggedEvent)

    override def log(level: Log.Level, event: Log.LoggedEvent): TestIO[D, Nothing, Unit] =
      TestIO(oldData => {
        val oldLoggingTestData = loggingTestDataField(oldData)

        val newLoggingTestData = oldLoggingTestData.log(level, event)

        val newData = setLoggingTestData(oldData, newLoggingTestData)

        Output(newData, Right(()))
      })
  }

}
