package au.id.tmm.ausvotes.shared.io.test

import java.time.{Duration, Instant}

import au.id.tmm.ausvotes.shared.io.actions.Log
import au.id.tmm.ausvotes.shared.io.actions.Log.LoggedEvent
import au.id.tmm.ausvotes.shared.io.test
import au.id.tmm.ausvotes.shared.io.test.datatraits.{CurrentTime, EnvVars, Logging}

final case class BasicTestData(
                                envVars: Map[String, String] = Map.empty,

                                loggedMessages: Map[Log.Level, List[LoggedEvent]] = Map.empty,
                                initialTime: Instant = Instant.EPOCH,
                                stepEachInvocation: Duration = Duration.ofSeconds(1),
                              )
  extends Logging[BasicTestData]
    with CurrentTime[BasicTestData]
    with EnvVars[BasicTestData] {

  override protected def copyWithLoggedMessages(loggedMessages: Map[Log.Level, List[LoggedEvent]]): BasicTestData =
    this.copy(loggedMessages = loggedMessages)

  override protected def copyWithInitialTime(initialTime: Instant): BasicTestData =
    this.copy(initialTime = initialTime)

}

object BasicTestData {
  type TestIO[+E, +A] = test.TestIO[E, A, BasicTestData]
}