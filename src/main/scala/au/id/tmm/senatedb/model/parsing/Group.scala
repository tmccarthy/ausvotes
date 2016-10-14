package au.id.tmm.senatedb.model.parsing

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.utilities.geo.australia.State

sealed trait BallotGroup extends Ordered[BallotGroup] {
  def code: String
  def index: Int

  override def compare(that: BallotGroup): Int = BallotGroup.ordering.compare(this, that)
}

final case class Group(election: SenateElection,
                       state: State,
                       code: String,
                       party: Option[Party]) extends BallotGroup {

require(code != Ungrouped.code, s"The code ${Ungrouped.code} is used for ungrouped")

  val index = {
    def charValue(char: Char) = char.toUpper - 'A'

    if (code.length == 1) {
      charValue(code.charAt(0))
    } else {
      (26 * (1 + charValue(code.charAt(0)))) + charValue(code.charAt(1))
    }
  }
}

case object Ungrouped extends BallotGroup {
  val code = "UG"
  val index = Int.MaxValue
}

object BallotGroup {
  val ordering: Ordering[BallotGroup] = new Ordering[BallotGroup] {
    override def compare(left: BallotGroup, right: BallotGroup): Int = left.index compareTo right.index
  }

  def lookupFrom(groups: Set[Group]): Map[String, BallotGroup] = (Set(Ungrouped) ++ groups)
    .groupBy(_.code)
    .mapValues(_.head)
}

object Group {
  def lookupFrom(groups: Set[Group]): Map[String, Group] = groups
    .groupBy(_.code)
    .mapValues(_.head)
}