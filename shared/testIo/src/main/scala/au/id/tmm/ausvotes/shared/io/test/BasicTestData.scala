package au.id.tmm.ausvotes.shared.io.test

import java.time.{Duration, Instant}

import au.id.tmm.ausvotes.shared.io.actions.Log
import au.id.tmm.ausvotes.shared.io.actions.Log.LoggedEvent
import au.id.tmm.ausvotes.shared.io.test

final case class BasicTestData(
                                envVars: Map[String, String] = Map.empty,

                                loggedMessages: Map[Log.Level, List[LoggedEvent]] = Map.empty,
                                initialTime: Instant = Instant.EPOCH,
                                stepEachInvocation: Duration = Duration.ofSeconds(1),
                              )
  extends TestDataUtils.Logging[BasicTestData]
    with TestDataUtils.CurrentTime[BasicTestData]
    with TestDataUtils.EnvVars[BasicTestData] {

  override protected def copyWithLoggedMessages(loggedMessages: Map[Log.Level, List[LoggedEvent]]): BasicTestData =
    this.copy(loggedMessages = loggedMessages)

  override protected def copyWithInitialTime(initialTime: Instant): BasicTestData =
    this.copy(initialTime = initialTime)

}

object BasicTestData {
  type TestIO[+E, +A] = test.TestIO[E, A, BasicTestData]
}