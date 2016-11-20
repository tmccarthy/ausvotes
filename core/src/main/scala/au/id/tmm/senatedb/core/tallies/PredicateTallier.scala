package au.id.tmm.senatedb.core.tallies

import au.id.tmm.senatedb.core.computations.BallotWithFacts

trait PredicateTallier extends PerBallotTallier {

  def shouldCount(ballotWithFacts: BallotWithFacts): Boolean

  override def countFor(ballotWithFacts: BallotWithFacts): Double = {
    if (shouldCount(ballotWithFacts)) {
      1.0d
    } else {
      0d
    }
  }
}
