package au.id.tmm.senatedb.core.tallies

import au.id.tmm.senatedb.core.fixtures.{Ballots, TestsBallotFacts}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountAtlAndBtlSpec extends ImprovedFlatSpec with TestsBallotFacts {

  it should "not count ballots that are formal above the line" in {
    assert(!CountAtlAndBtl.shouldCount(factsFor(Ballots.ACT.formalAtl)))
  }

  it should "count ballots that are formal both atl and btl" in {
    assert(CountAtlAndBtl.shouldCount(factsFor(Ballots.ACT.formalAtlAndBtl)))
  }

  it should "not count ballots that are formal btl" in {
    assert(!CountAtlAndBtl.shouldCount(factsFor(Ballots.ACT.formalBtl)))
  }

}
