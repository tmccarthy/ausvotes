package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.fixtures.{BallotFactsTestUtils, BallotFixture}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountDonkeyVotesSpec extends ImprovedFlatSpec {

  import BallotFactsTestUtils.ACT._

  val sut = BallotCounter.DonkeyVotes

  "the donkey votes count" should "count donkey votes" in {
    val donkeyVoteWithFacts = factsFor(BallotFixture.ACT.donkeyVote)

    assert(sut.isCounted(donkeyVoteWithFacts) === true)
  }

}
