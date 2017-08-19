package au.id.tmm.senatedb.core.tallies

import au.id.tmm.senatedb.core.fixtures.{BallotFixture, TestsBallotFacts}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountAtlAndBtlSpec extends ImprovedFlatSpec with TestsBallotFacts {

  val sut = BallotCounter.VotedAtlAndBtl

  it should "not count ballots that are formal above the line" in {
    assert(!sut.isCounted(factsFor(BallotFixture.ACT.formalAtl)))
  }

  it should "count ballots that are formal both atl and btl" in {
    assert(sut.isCounted(factsFor(BallotFixture.ACT.formalAtlAndBtl)))
  }

  it should "not count ballots that are formal btl" in {
    assert(!sut.isCounted(factsFor(BallotFixture.ACT.formalBtl)))
  }

}
