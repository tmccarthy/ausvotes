package au.id.tmm.senatedb.core.reporting

import au.id.tmm.senatedb.core.tallies.BallotCounter
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ExhaustedVotesReportBuilderSpec extends ImprovedFlatSpec with TestsStandardReportBuilder with TestsPartyTypeTable {
  override def expectedReportTitle: String = "Exhausted votes"

  override def expectedBallotCounter = BallotCounter.ExhaustedVotes

  override def expectedPrimaryCountColumnTitle: String = "Exhausted votes"

  override def sut: StandardReportBuilder = ExhaustedVotesReportBuilder

}
