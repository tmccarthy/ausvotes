package au.id.tmm.senatedb.tallies

import au.id.tmm.senatedb.tallies.Tallies.TraversableOps

final case class Tallies (asMap: Map[Tallier, TallyLike]) {

  require {
    asMap.forall {
      case (tallier, tally) => tallier.isOfTallyType(tally)
    }
  }

  def tallyBy(tallier: Tallier): tallier.TallyType = asMap(tallier).asInstanceOf[tallier.TallyType]

  def +(that: Tallies): Tallies = {
    val combinedTalliers = this.asMap.keySet ++ that.asMap.keySet

    combinedTalliers.toStream
      .flatMap(tallier => {
        val tallyFromThis = this.asMap.get(tallier)
        val tallyFromThat = that.asMap.get(tallier)

        val newTally: Option[TallyLike] = Stream(tallyFromThis, tallyFromThat)
          .flatten
          .reduceOption[TallyLike] {
            case (left: TallyLike, right: TallyLike) => left + right.asInstanceOf[left.SelfType]
          }

        newTally.map(tallier -> _)
      })
      .toTallies
  }
}

object Tallies {

  private val empty = new Tallies(Map.empty)

  def apply(): Tallies = empty

  def apply(talliesPerTallier: (Tallier, TallyLike)*): Tallies = Tallies(talliesPerTallier.toMap)

  final implicit class TraversableOps(talliesPerTallier: TraversableOnce[(Tallier, TallyLike)]) {
    def toTallies: Tallies = Tallies(talliesPerTallier.toMap)
  }
}