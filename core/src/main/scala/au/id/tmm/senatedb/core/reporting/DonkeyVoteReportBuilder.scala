package au.id.tmm.senatedb.core.reporting

import au.id.tmm.senatedb.core.reporting.PerBallotTallierReportBuilder.IncludesTableByPollingPlace
import au.id.tmm.senatedb.core.tallies.BallotCounter

object DonkeyVoteReportBuilder extends StandardReportBuilder with IncludesTableByPollingPlace {
  override def primaryCountColumnTitle: String = "Donkey votes"

  override def reportTitle: String = "Donkey votes"

  override def ballotCounter = BallotCounter.DonkeyVotes

  override def tableBuilders: Vector[TableBuilder] = super.tableBuilders :+ perPollingPlaceTableBuilder
}
