package au.id.tmm.senatedb.core.reporting

import au.id.tmm.senatedb.core.tallies.BallotCounter

object AtlAndBtlVoteReportBuilder extends StandardReportBuilder {
  override def primaryCountColumnTitle: String = "Ballots formal both above and below the line"

  override def reportTitle: String = "Ballots formal both above and below the line"

  override def ballotCounter = BallotCounter.VotedAtlAndBtl
}
