package au.id.tmm.senatedb.core.reporting
import au.id.tmm.senatedb.core.tallies.{CountDonkeyVotes, PredicateTallier}

object DonkeyVoteReportBuilder extends StandardReportBuilder {
  override def primaryCountColumnTitle: String = "Donkey votes"

  override def reportTitle: String = "Donkey votes"

  override def perBallotTallier: PredicateTallier = CountDonkeyVotes
}
