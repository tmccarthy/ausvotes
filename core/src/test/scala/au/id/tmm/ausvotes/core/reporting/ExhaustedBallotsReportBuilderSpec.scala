package au.id.tmm.ausvotes.core.reporting

import au.id.tmm.ausvotes.core.tallies.BallotCounter
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ExhaustedBallotsReportBuilderSpec extends ImprovedFlatSpec with TestsStandardReportBuilder with TestsPartyTypeTable {
  override def expectedReportTitle: String = "Exhausted ballots"

  override def expectedBallotCounter = BallotCounter.ExhaustedBallots

  override def expectedPrimaryCountColumnTitle: String = "Exhausted ballots"

  override def sut: StandardReportBuilder = ExhaustedBallotsReportBuilder
}
