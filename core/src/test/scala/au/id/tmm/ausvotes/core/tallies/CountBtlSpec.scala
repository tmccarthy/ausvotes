package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.fixtures.BallotFixture
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountBtlSpec extends ImprovedFlatSpec {

  import au.id.tmm.ausvotes.core.computations.BallotFactsTestUtils.ACT._

  val sut = BallotCounter.VotedBtl

  it should "not count ballots that are formal above the line" in {
    assert(!sut.isCounted(factsFor(BallotFixture.ACT.formalAtl)))
  }

  it should "count ballots that are formal both atl and btl" in {
    assert(sut.isCounted(factsFor(BallotFixture.ACT.formalAtlAndBtl)))
  }

  it should "count ballots that are formal btl" in {
    assert(sut.isCounted(factsFor(BallotFixture.ACT.formalBtl)))
  }

}
