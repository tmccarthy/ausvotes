package au.id.tmm.ausvotes.core.reporting

import au.id.tmm.ausvotes.core.tallies.{BallotCounter, BallotGrouping, TallierBuilder}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

trait TestsStandardReportBuilder { this: ImprovedFlatSpec =>

  def expectedReportTitle: String

  def expectedBallotCounter: BallotCounter

  def expectedPrimaryCountColumnTitle: String

  def sut: StandardReportBuilder

  it should "have the correct report title" in {
    assert(sut.reportTitle === expectedReportTitle)
  }

  it should "have the correct nationalTallier" in {
    assert(sut.nationalTallier === TallierBuilder.counting(expectedBallotCounter).overall())
  }

  it should "have the correct nationalPerFirstPreferenceTallier" in {
    assert(sut.nationalPerFirstPreferenceTallier === TallierBuilder.counting(expectedBallotCounter).groupedBy(BallotGrouping.FirstPreferencedPartyNationalEquivalent))
  }

  it should "have the correct perStateTallier" in {
    assert(sut.perStateTallier === TallierBuilder.counting(expectedBallotCounter).groupedBy(BallotGrouping.State))
  }

  it should "have the correct perDivisionTallier" in {
    assert(sut.perDivisionTallier === TallierBuilder.counting(expectedBallotCounter).groupedBy(BallotGrouping.Division))
  }

  it should "have the correct perFirstPreferencedGroupTallier" in {
    assert(sut.perFirstPreferencedGroupTallier === TallierBuilder.counting(expectedBallotCounter).groupedBy(BallotGrouping.State, BallotGrouping.FirstPreferencedGroup))
  }

  it should "have the correct count column title" in {
    assert(sut.primaryCountColumnTitle === expectedPrimaryCountColumnTitle)
  }
}
