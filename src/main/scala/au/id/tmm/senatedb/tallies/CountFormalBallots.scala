package au.id.tmm.senatedb.tallies
import au.id.tmm.senatedb.computations.BallotWithFacts

object CountFormalBallots extends PredicateTallier {
  override def shouldCount(ballotWithFacts: BallotWithFacts): Boolean = true
}
