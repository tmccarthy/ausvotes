package au.id.tmm.senatedb.tallies
import au.id.tmm.senatedb.computations.BallotWithFacts
import au.id.tmm.senatedb.model.computation.BallotExhaustion

object CountExhaustedVotes extends PerBallotTallier {
  override def countFor(ballotWithFacts: BallotWithFacts): Double = {
    ballotWithFacts.exhaustion match {
      case e: BallotExhaustion.Exhausted => e.value
      case BallotExhaustion.NotExhausted => 0d
    }
  }
}
