package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.fixtures.BallotFixture
import au.id.tmm.ausvotes.core.tallies.SenateTalliesUtils._
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountBtlSpec extends ImprovedFlatSpec {

  import au.id.tmm.ausvotes.core.computations.BallotFactsTestUtils.ACT._

  it should "not count ballots that are formal above the line" in {
    assert(!isCounted(SenateElectionTalliers.BallotTallier.VotedBtl)(factsFor(BallotFixture.ACT.formalAtl)))
  }

  it should "count ballots that are formal both atl and btl" in {
    assert(isCounted(SenateElectionTalliers.BallotTallier.VotedBtl)(factsFor(BallotFixture.ACT.formalAtlAndBtl)))
  }

  it should "count ballots that are formal btl" in {
    assert(isCounted(SenateElectionTalliers.BallotTallier.VotedBtl)(factsFor(BallotFixture.ACT.formalBtl)))
  }

}
