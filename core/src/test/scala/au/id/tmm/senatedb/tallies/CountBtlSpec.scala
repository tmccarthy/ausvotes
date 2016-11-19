package au.id.tmm.senatedb.tallies

import au.id.tmm.senatedb.fixtures.{Ballots, TestsBallotFacts}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountBtlSpec extends ImprovedFlatSpec with TestsBallotFacts {

  it should "not count ballots that are formal above the line" in {
    assert(!CountBtl.shouldCount(factsFor(Ballots.ACT.formalAtl)))
  }

  it should "count ballots that are formal both atl and btl" in {
    assert(CountBtl.shouldCount(factsFor(Ballots.ACT.formalAtlAndBtl)))
  }

  it should "count ballots that are formal btl" in {
    assert(CountBtl.shouldCount(factsFor(Ballots.ACT.formalBtl)))
  }

}
