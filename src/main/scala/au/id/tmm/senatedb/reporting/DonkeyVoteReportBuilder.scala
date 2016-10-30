package au.id.tmm.senatedb.reporting
import au.id.tmm.senatedb.tallies.{CountDonkeyVotes, PredicateTallier}

object DonkeyVoteReportBuilder extends StandardPredicateBasedReportBuilder {
  override def primaryCountColumnTitle: String = "Donkey votes"

  override def reportTitle: String = "Donkey votes"

  override def predicateTallier: PredicateTallier = CountDonkeyVotes
}
