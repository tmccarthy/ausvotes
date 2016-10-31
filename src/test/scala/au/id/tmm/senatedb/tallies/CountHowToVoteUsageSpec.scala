package au.id.tmm.senatedb.tallies

import au.id.tmm.senatedb.fixtures.{Ballots, TestsBallotFacts}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountHowToVoteUsageSpec extends ImprovedFlatSpec with TestsBallotFacts {

  "the how-to-vote usage counter" should "count ballots that used a how to vote card" in {
    val ballotWithFacts = factsFor(Ballots.ACT.usesHtv)

    assert(CountHowToVoteUsage.shouldCount(ballotWithFacts) === true)
  }

  it should "not count ballots that have not used a how to vote card" in {
    val ballotWithFacts = factsFor(Ballots.ACT.formalBtl)

    assert(CountHowToVoteUsage.shouldCount(ballotWithFacts) === false)
  }
}
