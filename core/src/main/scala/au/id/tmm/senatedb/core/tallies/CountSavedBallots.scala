package au.id.tmm.senatedb.core.tallies

import au.id.tmm.senatedb.core.computations.BallotWithFacts

object CountSavedBallots extends PredicateTallier {
  override def shouldCount(ballotWithFacts: BallotWithFacts): Boolean = {
    ballotWithFacts.savingsProvisionsUsed.nonEmpty
  }
}
