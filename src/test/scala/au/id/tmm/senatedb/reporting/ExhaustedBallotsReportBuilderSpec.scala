package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.tallies.{CountExhaustedBallots, PerBallotTallier}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ExhaustedBallotsReportBuilderSpec extends ImprovedFlatSpec with TestsStandardReportBuilder with TestsPartyTypeTable {
  override def expectedReportTitle: String = "Exhausted ballots"

  override def expectedPredicateTallier: PerBallotTallier = CountExhaustedBallots

  override def expectedPrimaryCountColumnTitle: String = "Exhausted ballots"

  override def sut: StandardReportBuilder = ExhaustedBallotsReportBuilder
}
