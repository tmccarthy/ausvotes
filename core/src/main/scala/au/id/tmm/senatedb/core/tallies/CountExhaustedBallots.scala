package au.id.tmm.senatedb.core.tallies
import au.id.tmm.senatedb.core.computations.BallotWithFacts
import au.id.tmm.senatedb.core.model.computation.BallotExhaustion

object CountExhaustedBallots extends PredicateTallier {
  override def shouldCount(ballotWithFacts: BallotWithFacts): Boolean = {
    ballotWithFacts.exhaustion match {
      case e: BallotExhaustion.Exhausted => true
      case BallotExhaustion.NotExhausted => false
    }
  }
}
