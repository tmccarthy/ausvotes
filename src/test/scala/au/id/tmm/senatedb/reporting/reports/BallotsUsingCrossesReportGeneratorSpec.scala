package au.id.tmm.senatedb.reporting.reports

import au.id.tmm.senatedb.fixtures.{Ballots, TestsBallotFacts}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class BallotsUsingCrossesReportGeneratorSpec extends ImprovedFlatSpec with TestsBallotFacts {

  it should "count ballots with a cross atl" in {
    assert(BallotsUsingCrossesReportGenerator.shouldCount(factsFor(Ballots.ACT.crossedAtl)) === true)
  }

  it should "count ballots with a cross btl" in {
    assert(BallotsUsingCrossesReportGenerator.shouldCount(factsFor(Ballots.ACT.crossedBtl)) === true)
  }

  it should "not count ballots with a tick" in {
    assert(BallotsUsingCrossesReportGenerator.shouldCount(factsFor(Ballots.ACT.tickedBtl)) === false)
  }
}
