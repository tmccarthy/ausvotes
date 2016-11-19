package au.id.tmm.senatedb.tallies
import au.id.tmm.senatedb.computations.BallotWithFacts
import au.id.tmm.senatedb.model.computation.BallotExhaustion

object CountExhaustedBallots extends PredicateTallier {
  override def shouldCount(ballotWithFacts: BallotWithFacts): Boolean = {
    ballotWithFacts.exhaustion match {
      case e: BallotExhaustion.Exhausted => true
      case BallotExhaustion.NotExhausted => false
    }
  }
}
