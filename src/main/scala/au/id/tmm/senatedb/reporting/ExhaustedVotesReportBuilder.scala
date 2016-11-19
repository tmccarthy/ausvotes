package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.tallies.{CountExhaustedVotes, PerBallotTallier}

object ExhaustedVotesReportBuilder extends StandardReportBuilder with IncludesTableByPartyType {
  override def primaryCountColumnTitle: String = "Exhausted votes"

  override def reportTitle: String = "Exhausted votes"

  override def perBallotTallier: PerBallotTallier = CountExhaustedVotes

}
