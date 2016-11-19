package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.tallies.{CountExhaustedVotes, PerBallotTallier}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ExhaustedVotesReportBuilderSpec extends ImprovedFlatSpec with TestsStandardReportBuilder with TestsPartyTypeTable {
  override def expectedReportTitle: String = "Exhausted votes"

  override def expectedPredicateTallier: PerBallotTallier = CountExhaustedVotes

  override def expectedPrimaryCountColumnTitle: String = "Exhausted votes"

  override def sut: StandardReportBuilder = ExhaustedVotesReportBuilder

}
