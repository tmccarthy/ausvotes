package au.id.tmm.senatedb.tallies
import au.id.tmm.senatedb.computations.BallotWithFacts

object CountBtl extends PredicateTallier {
  override def shouldCount(ballotWithFacts: BallotWithFacts): Boolean =
    ballotWithFacts.normalisedBallot.isNormalisedToBtl
}
