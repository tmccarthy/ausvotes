package au.id.tmm.senatedb.tallies

import au.id.tmm.senatedb.computations.BallotWithFacts

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
