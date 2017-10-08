package au.id.tmm.ausvotes.core.reporting

import au.id.tmm.ausvotes.core.tallies.BallotCounter
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class DonkeyVoteReportBuilderSpec extends ImprovedFlatSpec with TestsStandardReportBuilder {
  override def expectedReportTitle: String = "Donkey votes"

  override def expectedBallotCounter = BallotCounter.DonkeyVotes

  override def expectedPrimaryCountColumnTitle: String = "Donkey votes"

  override def sut: StandardReportBuilder = DonkeyVoteReportBuilder
}
