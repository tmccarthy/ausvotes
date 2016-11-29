package au.id.tmm.senatedb.tallies
import au.id.tmm.senatedb.computations.BallotWithFacts

object CountSavedBallots extends PredicateTallier {
  override def shouldCount(ballotWithFacts: BallotWithFacts): Boolean = {
    ballotWithFacts.savingsProvisionsUsed.nonEmpty
  }
}
