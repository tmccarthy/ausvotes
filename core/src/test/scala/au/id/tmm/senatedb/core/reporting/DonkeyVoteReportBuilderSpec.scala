package au.id.tmm.senatedb.core.reporting

import au.id.tmm.senatedb.core.tallies.{CountDonkeyVotes, PredicateTallier}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class DonkeyVoteReportBuilderSpec extends ImprovedFlatSpec with TestsStandardReportBuilder {
  override def expectedReportTitle: String = "Donkey votes"

  override def expectedPredicateTallier: PredicateTallier = CountDonkeyVotes

  override def expectedPrimaryCountColumnTitle: String = "Donkey votes"

  override def sut: StandardReportBuilder = DonkeyVoteReportBuilder
}
