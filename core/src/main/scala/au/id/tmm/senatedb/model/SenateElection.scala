package au.id.tmm.senatedb.model

import java.time.{LocalDate, Month}

final case class SenateElection private (date: LocalDate, states: Set[State], aecID: String) {

}

object SenateElection {
  val `2016` = SenateElection(LocalDate.of(2016, Month.JULY, 2), State.ALL_STATES, "20499")
}