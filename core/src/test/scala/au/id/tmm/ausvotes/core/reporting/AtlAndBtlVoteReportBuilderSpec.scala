package au.id.tmm.ausvotes.core.reporting

import au.id.tmm.ausvotes.core.tallies.BallotCounter
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class AtlAndBtlVoteReportBuilderSpec extends ImprovedFlatSpec with TestsStandardReportBuilder {
  override def expectedReportTitle: String = "Ballots formal both above and below the line"

  override def expectedBallotCounter = BallotCounter.VotedAtlAndBtl

  override def expectedPrimaryCountColumnTitle: String = "Ballots formal both above and below the line"

  override def sut: StandardReportBuilder = AtlAndBtlVoteReportBuilder
}
