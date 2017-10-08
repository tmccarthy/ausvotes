package au.id.tmm.ausvotes.core.reporting

import au.id.tmm.ausvotes.core.tallies.BallotCounter

object AtlVoteReportBuilder extends StandardReportBuilder {
  override def primaryCountColumnTitle: String = "Votes above the line"

  override def reportTitle: String = "Votes above the line"

  override def ballotCounter = BallotCounter.VotedAtl
}
