package au.id.tmm.senatedb.core.tallies
import au.id.tmm.senatedb.core.computations.BallotWithFacts

object CountAtlAndBtl extends PredicateTallier {
  override def shouldCount(ballotWithFacts: BallotWithFacts): Boolean = {
    ballotWithFacts.normalisedBallot.isFormalAtl && ballotWithFacts.normalisedBallot.isFormalBtl
  }
}
