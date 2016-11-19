package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.tallies.PerBallotTallier
import au.id.tmm.utilities.testing.ImprovedFlatSpec

trait TestsStandardReportBuilder { this: ImprovedFlatSpec =>

  def expectedReportTitle: String

  def expectedPredicateTallier: PerBallotTallier

  def expectedPrimaryCountColumnTitle: String

  def sut: StandardReportBuilder

  it should "have the correct report title" in {
    assert(sut.reportTitle === expectedReportTitle)
  }

  it should "have the correct nationalTallier" in {
    assert(sut.nationalTallier === expectedPredicateTallier.Nationally)
  }

  it should "have the correct nationalPerFirstPreferenceTallier" in {
    assert(sut.nationalPerFirstPreferenceTallier === expectedPredicateTallier.NationallyByFirstPreference)
  }

  it should "have the correct perStateTallier" in {
    assert(sut.perStateTallier === expectedPredicateTallier.ByState)
  }

  it should "have the correct perDivisionTallier" in {
    assert(sut.perDivisionTallier === expectedPredicateTallier.ByDivision)
  }

  it should "have the correct perFirstPreferencedGroupTallier" in {
    assert(sut.perFirstPreferencedGroupTallier === expectedPredicateTallier.ByFirstPreferencedGroup)
  }

  it should "have the correct count column title" in {
    assert(sut.primaryCountColumnTitle === expectedPrimaryCountColumnTitle)
  }
}
