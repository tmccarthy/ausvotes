package au.id.tmm.ausvotes.core.reporting

import au.id.tmm.ausvotes.core.tallies.BallotCounter
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class AtlVoteReportBuilderSpec extends ImprovedFlatSpec with TestsStandardReportBuilder {
  override def expectedReportTitle: String = "Votes above the line"

  override def expectedBallotCounter = BallotCounter.VotedAtl

  override def expectedPrimaryCountColumnTitle: String = "Votes above the line"

  override def sut: StandardReportBuilder = AtlVoteReportBuilder
}
