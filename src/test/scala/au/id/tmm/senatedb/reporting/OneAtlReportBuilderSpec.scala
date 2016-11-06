package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.tallies.{CountOneAtl, PredicateTallier}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class OneAtlReportBuilderSpec extends ImprovedFlatSpec with TestsStandardPredicateBasedReportBuilder {
  override def expectedReportTitle: String = "Ballots with only '1' above the line"

  override def expectedPredicateTallier: PredicateTallier = CountOneAtl

  override def expectedPrimaryCountColumnTitle: String = "Ballots with only '1' above the line"

  override def sut: StandardReportBuilder = OneAtlReportBuilder
}
