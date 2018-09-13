package au.id.tmm.ausvotes.shared.io.test

import java.time.{Duration, Instant}

import au.id.tmm.ausvotes.shared.io.actions.Log
import au.id.tmm.ausvotes.shared.io.actions.Log.LoggedEvent

object TestDataUtils {

  trait Logging[D] {
    def loggedMessages: Map[Log.Level, List[LoggedEvent]]
    protected def copyWithLoggedMessages(loggedMessages: Map[Log.Level, List[LoggedEvent]]): D

    def log(level: Log.Level, event: LoggedEvent): D =
      copyWithLoggedMessages(
        this.loggedMessages.updated(level, this.loggedMessages.getOrElse(level, Nil) :+ event)
      )
  }

  trait CurrentTime[D] {
    def initialTime: Instant
    def stepEachInvocation: Duration
    protected def copyWithInitialTime(initialTime: Instant): D

    def increment: (Instant, D) =
      (initialTime, this.copyWithInitialTime(initialTime.plus(stepEachInvocation)))
  }

  trait EnvVars[D] {
    def envVars: Map[String, String]
  }

}