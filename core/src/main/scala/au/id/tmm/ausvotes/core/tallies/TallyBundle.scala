package au.id.tmm.ausvotes.core.tallies

final case class TallyBundle(asMap: Map[Tallier, Tally]) {

  require {
    asMap.forall {
      case (tallier, tally) => tallier.isOfProducedTallyType(tally)
    }
  }

  def tallyProducedBy(tallier: Tallier): tallier.ProducedTallyType = asMap(tallier).asInstanceOf[tallier.ProducedTallyType]

  def +(that: TallyBundle): TallyBundle = {
    val combinedTalliers = this.asMap.keySet ++ that.asMap.keySet

    val combinationAsMap = combinedTalliers.toStream
      .flatMap(tallier => {
        val tallyFromThis = this.asMap.get(tallier)
        val tallyFromThat = that.asMap.get(tallier)

        val newTally: Option[Tally] = Stream(tallyFromThis, tallyFromThat)
          .flatten
          .reduceOption[Tally] {
          case (left: Tally, right: Tally) => left + right.asInstanceOf[left.SelfType]
        }

        newTally.map(tallier -> _)
      })
      .toMap

    TallyBundle(combinationAsMap)
  }

}

object TallyBundle {
  def apply(entries: (Tallier, Tally)*): TallyBundle = TallyBundle(entries.toMap)

  final implicit class TraversableOps(talliesPerTallier: TraversableOnce[(Tallier, Tally)]) {
    def toTallyBundle: TallyBundle = TallyBundle(talliesPerTallier.toMap)
  }
}