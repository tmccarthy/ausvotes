package au.id.tmm.senatedb.core.tallies
import au.id.tmm.senatedb.core.computations.BallotWithFacts
import au.id.tmm.senatedb.core.model.computation.BallotExhaustion

object CountExhaustedVotes extends PerBallotTallier {
  override def countFor(ballotWithFacts: BallotWithFacts): Double = {
    ballotWithFacts.exhaustion match {
      case e: BallotExhaustion.Exhausted => e.value
      case BallotExhaustion.NotExhausted => 0d
    }
  }
}
