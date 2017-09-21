package au.id.tmm.senatedb.core.tallies

import au.id.tmm.senatedb.core.fixtures.{BallotFixture, TestsBallotFacts}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountDonkeyVotesSpec extends ImprovedFlatSpec with TestsBallotFacts {

  val sut = BallotCounter.DonkeyVotes

  "the donkey votes count" should "count donkey votes" in {
    val donkeyVoteWithFacts = factsFor(BallotFixture.ACT.donkeyVote)

    assert(sut.isCounted(donkeyVoteWithFacts) === true)
  }

}
