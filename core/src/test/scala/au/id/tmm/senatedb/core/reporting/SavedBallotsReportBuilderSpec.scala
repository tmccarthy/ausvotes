package au.id.tmm.senatedb.core.reporting

import au.id.tmm.senatedb.core.tallies.BallotCounter
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class SavedBallotsReportBuilderSpec extends ImprovedFlatSpec with TestsStandardReportBuilder {
  override def expectedReportTitle: String = "Saved ballots"

  override def expectedBallotCounter = BallotCounter.UsedSavingsProvision

  override def expectedPrimaryCountColumnTitle: String = "Saved ballots"

  override def sut: StandardReportBuilder = SavedBallotsReportBuilder
}
