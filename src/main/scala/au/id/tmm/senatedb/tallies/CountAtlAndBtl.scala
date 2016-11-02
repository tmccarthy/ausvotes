package au.id.tmm.senatedb.tallies
import au.id.tmm.senatedb.computations.BallotWithFacts

object CountAtlAndBtl extends PredicateTallier {
  override def shouldCount(ballotWithFacts: BallotWithFacts): Boolean = {
    ballotWithFacts.normalisedBallot.isFormalAtl && ballotWithFacts.normalisedBallot.isFormalBtl
  }
}
