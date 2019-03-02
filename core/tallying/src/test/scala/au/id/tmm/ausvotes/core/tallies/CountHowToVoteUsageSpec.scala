package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.fixtures.BallotFixture
import au.id.tmm.ausvotes.core.tallies.SenateTalliesUtils._
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountHowToVoteUsageSpec extends ImprovedFlatSpec {

  import au.id.tmm.ausvotes.core.computations.BallotFactsTestUtils.ACT._

  "the how-to-vote usage counter" should "count ballots that used a how to vote card" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.usesHtv)

    assert(isCounted(SenateElectionTalliers.BallotTallier.UsedHowToVoteCard)(ballotWithFacts) === true)
  }

  it should "not count ballots that have not used a how to vote card" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.formalBtl)

    assert(isCounted(SenateElectionTalliers.BallotTallier.UsedHowToVoteCard)(ballotWithFacts) === false)
  }
}
