package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.tallies.{CountExhaustedBallots, PerBallotTallier}

object ExhaustedBallotsReportBuilder extends StandardReportBuilder with IncludesTableByPartyType {
  override def primaryCountColumnTitle: String = "Exhausted ballots"

  override def reportTitle: String = "Exhausted ballots"

  override def perBallotTallier: PerBallotTallier = CountExhaustedBallots
}
