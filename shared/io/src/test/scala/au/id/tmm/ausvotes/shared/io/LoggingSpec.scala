package au.id.tmm.ausvotes.shared.io

import au.id.tmm.ausvotes.shared.io.Logging.{LoggingOps, timedLog}
import au.id.tmm.ausvotes.shared.io.actions.Log
import au.id.tmm.ausvotes.shared.io.actions.Log.LoggedEvent
import au.id.tmm.ausvotes.shared.io.test.{BasicTestData, TestIO}
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import org.scalatest.Assertion

class LoggingSpec extends ImprovedFlatSpec {

  private def testLogging[E, A](
                                 initialTestData: BasicTestData = BasicTestData(),
                                 testLogic: BasicTestData.TestIO[E, A],
                                 expectedLoggedLevel: Log.Level,
                                 expectedLoggedEvent: LoggedEvent,
                               ): Assertion = {
    val (finalTestData, _) = testLogic.run(initialTestData)

    assert(finalTestData.loggedMessages === Map(expectedLoggedLevel -> List(expectedLoggedEvent)))
  }

  "the timed logging of a block" should "log for a successful execution" in {
    testLogging[Nothing, Unit](
      testLogic = timedLog[BasicTestData.TestIO, Nothing, Unit]("TEST_EVENT", "key" -> "value")(Right(Unit)),
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
      testLogic = timedLog[BasicTestData.TestIO, Unit, Nothing]("TEST_EVENT", "key" -> "value")(Left(Unit)),
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
      testLogic = timedLog[BasicTestData.TestIO, RuntimeException, Nothing]("TEST_EVENT", "key" -> "value")(Left(exception)),
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

  private def timedLogTestIO[E, A](testIO: BasicTestData.TestIO[E, A])(eventId: String, kvPairs: (String, String)*) =
    new LoggingOps[BasicTestData.TestIO, E, A](testIO).timedLog(eventId, kvPairs: _*)

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
