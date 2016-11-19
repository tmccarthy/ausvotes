package au.id.tmm.senatedb.tallies
import au.id.tmm.senatedb.computations.BallotWithFacts

object CountDonkeyVotes extends PredicateTallier {
  override def shouldCount(ballotWithFacts: BallotWithFacts): Boolean = ballotWithFacts.isDonkeyVote
}
