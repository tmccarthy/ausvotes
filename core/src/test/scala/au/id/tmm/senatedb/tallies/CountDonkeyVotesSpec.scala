package au.id.tmm.senatedb.tallies

import au.id.tmm.senatedb.fixtures.{Ballots, TestsBallotFacts}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountDonkeyVotesSpec extends ImprovedFlatSpec with TestsBallotFacts {

  "the donkey votes count" should "count donkey votes" in {
    val donkeyVoteWithFacts = factsFor(Ballots.ACT.donkeyVote)

    assert(CountDonkeyVotes.shouldCount(donkeyVoteWithFacts) === true)
  }

}
