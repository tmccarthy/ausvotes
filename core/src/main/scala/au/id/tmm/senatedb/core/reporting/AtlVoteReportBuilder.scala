package au.id.tmm.senatedb.core.reporting

import au.id.tmm.senatedb.core.tallies.BallotCounter

object AtlVoteReportBuilder extends StandardReportBuilder {
  override def primaryCountColumnTitle: String = "Votes above the line"

  override def reportTitle: String = "Votes above the line"

  override def ballotCounter = BallotCounter.VotedAtl
}
