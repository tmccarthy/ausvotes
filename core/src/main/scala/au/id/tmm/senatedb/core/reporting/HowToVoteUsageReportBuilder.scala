package au.id.tmm.senatedb.core.reporting

import au.id.tmm.senatedb.core.tallies.BallotCounter

object HowToVoteUsageReportBuilder extends StandardReportBuilder {
  override def primaryCountColumnTitle: String = "Ballots matching an HTV card"

  override def reportTitle: String = "How to vote card usage"

  override def ballotCounter = BallotCounter.UsedHowToVoteCard
}
