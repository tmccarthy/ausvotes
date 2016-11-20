package au.id.tmm.senatedb.core.tallies
import au.id.tmm.senatedb.core.computations.BallotWithFacts

object CountFormalBallots extends PredicateTallier {
  override def shouldCount(ballotWithFacts: BallotWithFacts): Boolean = true
}
