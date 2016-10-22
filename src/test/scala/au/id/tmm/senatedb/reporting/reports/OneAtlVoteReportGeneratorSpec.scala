package au.id.tmm.senatedb.reporting.reports

import au.id.tmm.senatedb.fixtures.{Ballots, TestsBallotFacts}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class OneAtlVoteReportGeneratorSpec extends ImprovedFlatSpec with TestsBallotFacts {

  it should "not count ballots with more than one vote atl" in {
    assert(OneAtlVoteReportGenerator.shouldCount(factsFor(Ballots.ACT.formalAtl)) === false)
  }

  it should "not count ballots with votes btl" in {
    assert(OneAtlVoteReportGenerator.shouldCount(factsFor(Ballots.ACT.oneAtlFormalBtl)) === false)
  }

  it should "count ballots with one vote atl" in {
    assert(OneAtlVoteReportGenerator.shouldCount(factsFor(Ballots.ACT.oneAtl)) === true)
  }
}
