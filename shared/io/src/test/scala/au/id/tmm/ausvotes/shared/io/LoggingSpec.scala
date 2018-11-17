package au.id.tmm.ausvotes.shared.io

import au.id.tmm.ausvotes.shared.io.Logging.{LoggingOps, timedLog}
import au.id.tmm.ausvotes.shared.io.actions.Log
import au.id.tmm.ausvotes.shared.io.actions.Log.LoggedEvent
import au.id.tmm.ausvotes.shared.io.test.BasicTestData.BasicTestIO
import au.id.tmm.ausvotes.shared.io.test.TestIO.Output
import au.id.tmm.ausvotes.shared.io.test.{BasicTestData, TestIO}
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import org.scalatest.Assertion

class LoggingSpec extends ImprovedFlatSpec {

  private def testLogging[E, A](
                                 initialTestData: BasicTestData = BasicTestData(),
                                 testLogic: BasicTestIO[E, A],
                                 expectedLoggedLevel: Log.Level,
                                 expectedLoggedEvent: LoggedEvent,
                               ): Assertion = {
    val Output(finalTestData, _) = testLogic.run(initialTestData)

    assert(finalTestData.loggingTestData.loggedMessages === Map(expectedLoggedLevel -> List(expectedLoggedEvent)))
  }

  "the timed logging of a block" should "log for a successful execution" in {
    testLogging[Nothing, Unit](
      testLogic = timedLog[BasicTestIO, Nothing, Unit]("TEST_EVENT", "key" -> "value")(Right(Unit)),
      expectedLoggedLevel = Log.Level.Info,
      expectedLoggedEvent = LoggedEvent(
        "TEST_EVENT",
        List(
          "key" -> "value",
          "successful" -> true,
          "duration" -> 1000,
        ),
        exception = None,
      ),
    )
  }

  it should "log for a failed execution" in {
    testLogging[Unit, Nothing](
      testLogic = timedLog[BasicTestIO, Unit, Nothing]("TEST_EVENT", "key" -> "value")(Left(Unit)),
      expectedLoggedLevel = Log.Level.Error,
      expectedLoggedEvent = LoggedEvent(
        "TEST_EVENT",
        List(
          "key" -> "value",
          "successful" -> false,
          "duration" -> 1000,
        ),
        exception = None,
      ),
    )
  }

  it should "log for a failed execution that contains an exception" in {
    val exception = new RuntimeException

    testLogging[RuntimeException, Nothing](
      testLogic = timedLog[BasicTestIO, RuntimeException, Nothing]("TEST_EVENT", "key" -> "value")(Left(exception)),
      expectedLoggedLevel = Log.Level.Error,
      expectedLoggedEvent = LoggedEvent(
        "TEST_EVENT",
        List(
          "key" -> "value",
          "successful" -> false,
          "duration" -> 1000,
        ),
        exception = Some(exception),
      ),
    )
  }

  private def timedLogTestIO[E, A](testIO: BasicTestIO[E, A])(eventId: String, kvPairs: (String, String)*) =
    new LoggingOps[BasicTestIO, E, A](testIO).timedLog(eventId, kvPairs: _*)

  "the timed logging of an IO execution" should "log for a successful execution" in {
    testLogging[Nothing, Unit](
      testLogic = timedLogTestIO(TestIO.pure[Unit, BasicTestData](Unit))("TEST_EVENT", "key" -> "value"),
      expectedLoggedLevel = Log.Level.Info,
      expectedLoggedEvent = LoggedEvent(
        "TEST_EVENT",
        List(
          "key" -> "value",
          "successful" -> true,
          "duration" -> 1000,
        ),
        exception = None,
      ),
    )
  }

  it should "log for a failed execution" in {
    testLogging[Unit, Nothing](
      testLogic = timedLogTestIO(TestIO.leftPure[Unit, BasicTestData](Unit))("TEST_EVENT", "key" -> "value"),
      expectedLoggedLevel = Log.Level.Error,
      expectedLoggedEvent = LoggedEvent(
        "TEST_EVENT",
        List(
          "key" -> "value",
          "successful" -> false,
          "duration" -> 1000,
        ),
        exception = None,
      ),
    )
  }

  it should "log for a failed execution that contains an exception" in {
    val exception = new RuntimeException

    testLogging[RuntimeException, Nothing](
      testLogic = timedLogTestIO(TestIO.leftPure[RuntimeException, BasicTestData](exception))("TEST_EVENT", "key" -> "value"),
      expectedLoggedLevel = Log.Level.Error,
      expectedLoggedEvent = LoggedEvent(
        "TEST_EVENT",
        List(
          "key" -> "value",
          "successful" -> false,
          "duration" -> 1000,
        ),
        exception = Some(exception),
      ),
    )
  }
}
