package au.id.tmm.ausvotes.shared.io.test.datatraits

import au.id.tmm.ausvotes.shared.io.actions.Log
import au.id.tmm.ausvotes.shared.io.actions.Log.LoggedEvent

trait Logging[D] {
  def loggedMessages: Map[Log.Level, List[LoggedEvent]]
  protected def copyWithLoggedMessages(loggedMessages: Map[Log.Level, List[LoggedEvent]]): D

  def log(level: Log.Level, event: LoggedEvent): D =
    copyWithLoggedMessages(
      this.loggedMessages.updated(level, this.loggedMessages.getOrElse(level, Nil) :+ event)
    )
}
