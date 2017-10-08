package au.id.tmm.ausvotes.core.model.parsing

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.utilities.geo.australia.State

sealed trait BallotGroup extends Ordered[BallotGroup] {
  def code: String
  def index: Int
  def state: State

  override def compare(that: BallotGroup): Int = BallotGroup.ordering.compare(this, that)
}

final case class Group(election: SenateElection,
                       state: State,
                       code: String,
                       party: Party) extends BallotGroup {

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

final case class Ungrouped(state: State) extends BallotGroup {
  val code = Ungrouped.code
  val index = Int.MaxValue
}

object Ungrouped {
  val code = "UG"
}

object BallotGroup {
  val ordering: Ordering[BallotGroup] = new Ordering[BallotGroup] {
    override def compare(left: BallotGroup, right: BallotGroup): Int = left.index compareTo right.index
  }
}