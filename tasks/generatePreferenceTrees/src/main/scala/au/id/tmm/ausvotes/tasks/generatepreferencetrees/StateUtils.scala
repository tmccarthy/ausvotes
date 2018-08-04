package au.id.tmm.ausvotes.tasks.generatepreferencetrees

import au.id.tmm.utilities.geo.australia.State

object StateUtils {

  val numBallots: Map[State, Int] = Map(
    State.NSW -> 4705270,
    State.VIC -> 3653736,
    State.QLD -> 2818997,
    State.WA  -> 1413553,
    State.SA  -> 1097710,
    State.TAS -> 351380,
    State.ACT -> 282045,
    State.NT  -> 105539,
  )

}
