package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.fixtures.BallotFixture
import au.id.tmm.ausvotes.core.tallies.SenateTalliesUtils._
import org.scalatest.FlatSpec

class CountDonkeyVotesSpec extends FlatSpec {

  import au.id.tmm.ausvotes.core.computations.BallotFactsTestUtils.ACT._

  "the donkey votes count" should "count donkey votes" in {
    val donkeyVoteWithFacts = factsFor(BallotFixture.ACT.donkeyVote)

    assert(isCounted(SenateElectionTalliers.BallotTallier.DonkeyVotes)(donkeyVoteWithFacts) === true)
  }

}
