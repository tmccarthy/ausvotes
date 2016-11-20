package au.id.tmm.senatedb.core.reporting

import au.id.tmm.senatedb.core.tallies.{CountAtlAndBtl, PredicateTallier}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class AtlAndBtlVoteReportBuilderSpec extends ImprovedFlatSpec with TestsStandardReportBuilder {
  override def expectedReportTitle: String = "Ballots formal both above and below the line"

  override def expectedPredicateTallier: PredicateTallier = CountAtlAndBtl

  override def expectedPrimaryCountColumnTitle: String = "Ballots formal both above and below the line"

  override def sut: StandardReportBuilder = AtlAndBtlVoteReportBuilder
}
