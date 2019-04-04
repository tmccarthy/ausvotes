package au.id.tmm.ausvotes.core.tallies.redo

import cats.Monoid

object syntax {

  implicit class TallierOps[B, A : Monoid](ballotTallier: BallotTallier[B, A]) {
    def groupingBy[G](ballotGrouper: BallotGrouper[B, G]): BallotTallier.GroupingTallier[B, G, A] =
      BallotTallier.GroupingTallier.apply(ballotGrouper, ballotTallier)
  }

}
