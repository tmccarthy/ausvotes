package au.id.tmm.senatedb.core.tallies

import au.id.tmm.senatedb.core.fixtures.{Ballots, TestsBallotFacts}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountAtlSpec extends ImprovedFlatSpec with TestsBallotFacts {

  it should "count ballots that are formal above the line" in {
    assert(CountAtl.shouldCount(factsFor(Ballots.ACT.formalAtl)))
  }

  it should "not count ballots that are formal both atl and btl" in {
    assert(!CountAtl.shouldCount(factsFor(Ballots.ACT.formalAtlAndBtl)))
  }

  it should "not count ballots that are formal btl" in {
    assert(!CountAtl.shouldCount(factsFor(Ballots.ACT.formalBtl)))
  }

}
