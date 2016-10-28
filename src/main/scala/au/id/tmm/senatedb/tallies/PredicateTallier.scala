package au.id.tmm.senatedb.tallies

import au.id.tmm.senatedb.computations.BallotWithFacts

trait PredicateTallier {

  def shouldCount(ballotWithFacts: BallotWithFacts): Boolean

  object Nationally extends Tallier.SimpleTallier {
    override def tally(ballotsWithFacts: Vector[BallotWithFacts]): SimpleTally =
      SimpleTally(ballotsWithFacts.count(shouldCount))
  }

}
