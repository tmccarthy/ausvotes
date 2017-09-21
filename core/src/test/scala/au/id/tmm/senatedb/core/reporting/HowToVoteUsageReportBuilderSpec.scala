package au.id.tmm.senatedb.core.reporting

import au.id.tmm.senatedb.core.tallies.BallotCounter
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class HowToVoteUsageReportBuilderSpec extends ImprovedFlatSpec with TestsStandardReportBuilder {
  override def expectedReportTitle: String = "How to vote card usage"

  override def expectedBallotCounter = BallotCounter.UsedHowToVoteCard

  override def expectedPrimaryCountColumnTitle: String = "Ballots matching an HTV card"

  override def sut: StandardReportBuilder = HowToVoteUsageReportBuilder
}
