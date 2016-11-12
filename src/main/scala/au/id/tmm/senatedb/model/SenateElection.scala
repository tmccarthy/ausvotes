package au.id.tmm.senatedb.model

import java.time.{LocalDate, Month}

import au.id.tmm.utilities.geo.australia.State

final case class SenateElection private (date: LocalDate, states: Set[State], aecID: String) extends Ordered[SenateElection] {
  override def toString: String = s"${date.getYear} election"

  override def compare(that: SenateElection): Int = SenateElection.ordering.compare(this, that)
}

object SenateElection {

  val ordering: Ordering[SenateElection] = new Ordering[SenateElection] {
    override def compare(left: SenateElection, right: SenateElection): Int = left.date compareTo right.date
  }

  val `2016` = SenateElection(LocalDate.of(2016, Month.JULY, 2), State.ALL_STATES, "20499")
  val `2013` = SenateElection(LocalDate.of(2013, Month.SEPTEMBER, 7), State.ALL_STATES, "17496")
}