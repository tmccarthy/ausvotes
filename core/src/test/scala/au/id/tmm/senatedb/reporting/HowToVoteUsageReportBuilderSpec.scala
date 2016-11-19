package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.tallies.{CountHowToVoteUsage, PredicateTallier}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class HowToVoteUsageReportBuilderSpec extends ImprovedFlatSpec with TestsStandardReportBuilder {
  override def expectedReportTitle: String = "How to vote card usage"

  override def expectedPredicateTallier: PredicateTallier = CountHowToVoteUsage

  override def expectedPrimaryCountColumnTitle: String = "Ballots matching an HTV card"

  override def sut: StandardReportBuilder = HowToVoteUsageReportBuilder
}
