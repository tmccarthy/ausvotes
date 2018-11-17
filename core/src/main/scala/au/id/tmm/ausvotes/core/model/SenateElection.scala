package au.id.tmm.ausvotes.core.model

import java.time.LocalDate
import java.time.Month._

import au.id.tmm.utilities.geo.australia.State

trait SenateElection extends Ordered[SenateElection] {
  def date: LocalDate
  def states: Set[State] = State.ALL_STATES
  def aecID: Int
  def name: String = s"${date.getYear} election"
  def doubleDissolution: Boolean = false

  override def compare(that: SenateElection): Int = this.date compareTo that.date

  override def toString: String = name

  def id: String
}

object SenateElection {

  case object `2016` extends SenateElection {
    override def date: LocalDate = LocalDate.of(2016, JULY, 2)

    override def aecID: Int = 20499

    override val id: String = "2016"

    override def doubleDissolution: Boolean = true
  }

  case object `2014 WA` extends SenateElection {
    override def date: LocalDate = LocalDate.of(2014, APRIL, 5)

    override def states: Set[State] = Set(State.WA)

    override def aecID: Int = 17875

    override def name: String = "2014 WA Senate election"

    override val id: String = "2014WA"
  }

  case object `2013` extends SenateElection {
    override def date: LocalDate = LocalDate.of(2013, SEPTEMBER, 7)

    override def aecID: Int = 17496

    override val id: String = "2013"
  }

  def forId(id: String): Option[SenateElection] = id match {
    case `2016`.id => Some(`2016`)
    case `2014 WA`.id => Some(`2014 WA`)
    case `2013`.id => Some(`2013`)
    case _ => None
  }

  type StateAtElection = (SenateElection, State)

}
