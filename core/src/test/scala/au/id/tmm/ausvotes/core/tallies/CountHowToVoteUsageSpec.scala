package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.fixtures.{BallotFactsTestUtils, BallotFixture}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountHowToVoteUsageSpec extends ImprovedFlatSpec {

  import BallotFactsTestUtils.ACT._

  val sut = BallotCounter.UsedHowToVoteCard

  "the how-to-vote usage counter" should "count ballots that used a how to vote card" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.usesHtv)

    assert(sut.isCounted(ballotWithFacts) === true)
  }

  it should "not count ballots that have not used a how to vote card" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.formalBtl)

    assert(sut.isCounted(ballotWithFacts) === false)
  }
}
