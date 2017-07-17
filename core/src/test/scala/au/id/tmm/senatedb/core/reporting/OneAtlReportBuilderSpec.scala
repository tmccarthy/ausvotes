package au.id.tmm.senatedb.core.reporting

import au.id.tmm.senatedb.core.tallies.BallotCounter
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class OneAtlReportBuilderSpec extends ImprovedFlatSpec with TestsStandardReportBuilder {
  override def expectedReportTitle: String = "Ballots with only '1' above the line"

  override def expectedBallotCounter = BallotCounter.Voted1Atl

  override def expectedPrimaryCountColumnTitle: String = "Ballots with only '1' above the line"

  override def sut: StandardReportBuilder = OneAtlReportBuilder
}
