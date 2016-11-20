package au.id.tmm.senatedb.core.reporting

import au.id.tmm.senatedb.core.tallies.{CountSavedBallots, PredicateTallier}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class SavedBallotsReportBuilderSpec extends ImprovedFlatSpec with TestsStandardReportBuilder {
  override def expectedReportTitle: String = "Saved ballots"

  override def expectedPredicateTallier: PredicateTallier = CountSavedBallots

  override def expectedPrimaryCountColumnTitle: String = "Saved ballots"

  override def sut: StandardReportBuilder = SavedBallotsReportBuilder
}
