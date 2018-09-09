package au.id.tmm.ausvotes.shared.io

import java.time.{Duration, Instant}

import au.id.tmm.ausvotes.shared.io.test.TestDataUtils
import au.id.tmm.ausvotes.shared.io.typeclasses.Log
import au.id.tmm.ausvotes.shared.io.typeclasses.Log.LoggedEvent

final case class TestData(
                           loggedMessages: Map[Log.Level, List[LoggedEvent]] = Map.empty,
                           initialTime: Instant = Instant.EPOCH,
                           stepEachInvocation: Duration = Duration.ofSeconds(1),
                         )
  extends TestDataUtils.Logging[TestData]
    with TestDataUtils.CurrentTime[TestData] {

  override protected def copyWithLoggedMessages(loggedMessages: Map[Log.Level, List[LoggedEvent]]): TestData =
    this.copy(loggedMessages = loggedMessages)

  override protected def copyWithInitialTime(initialTime: Instant): TestData =
    this.copy(initialTime = initialTime)

}