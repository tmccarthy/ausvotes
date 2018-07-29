package au.id.tmm.ausvotes.tasks.generatepreferencetrees

import au.id.tmm.utilities.geo.australia.State

object Main {

  def main(args: Array[String]): Unit = {
  }

  private def numBallotsHint(state: State): Int = state match {
    case State.NSW => 4705270
    case State.VIC => 3653736
    case State.QLD => 2818997
    case State.WA  => 1413553
    case State.SA  => 1097710
    case State.TAS => 351380
    case State.ACT => 282045
    case State.NT  => 105539
  }
}
