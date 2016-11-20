package au.id.tmm.senatedb.core.tallies
import au.id.tmm.senatedb.core.computations.BallotWithFacts

object CountAtl extends PredicateTallier {
  override def shouldCount(ballotWithFacts: BallotWithFacts): Boolean =
    ballotWithFacts.normalisedBallot.isNormalisedToAtl
}
