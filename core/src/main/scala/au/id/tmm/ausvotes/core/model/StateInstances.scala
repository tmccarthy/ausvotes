package au.id.tmm.ausvotes.core.model

import au.id.tmm.utilities.geo.australia.State

object StateInstances {

  val orderStatesByPopulation: Ordering[State] = new Ordering[State] {
    private def sizeOf(state: State): Int = state match {
      case State.NSW => 8
      case State.VIC => 7
      case State.QLD => 6
      case State.WA => 5
      case State.SA => 4
      case State.TAS => 3
      case State.ACT => 2
      case State.NT => 1
    }

    override def compare(left: State, right: State): Int = sizeOf(right) - sizeOf(left)
  }

}
