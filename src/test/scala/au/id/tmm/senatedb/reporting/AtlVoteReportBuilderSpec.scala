package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.tallies.{CountAtl, PredicateTallier}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class AtlVoteReportBuilderSpec extends ImprovedFlatSpec with TestsStandardPredicateBasedReportBuilder {
  override def expectedReportTitle: String = "Votes above the line"

  override def expectedPredicateTallier: PredicateTallier = CountAtl

  override def expectedPrimaryCountColumnTitle: String = "Votes above the line"

  override def sut: StandardReportBuilder = AtlVoteReportBuilder
}
