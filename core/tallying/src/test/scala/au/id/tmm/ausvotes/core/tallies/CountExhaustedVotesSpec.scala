package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.fixtures.BallotFixture
import au.id.tmm.ausvotes.core.tallies.SenateTalliesUtils._
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountExhaustedVotesSpec extends ImprovedFlatSpec {

  import au.id.tmm.ausvotes.core.computations.BallotFactsTestUtils.ACT._

  "the exhausted ballots count" should "count any exhausted ballot" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.exhaustingBallot)

    assert(tallyFor(SenateElectionTalliers.BallotTallier.ExhaustedVotes)(ballotWithFacts) === 0.113066455002141d)
  }

  it should "not count a ballot that is not exhausting" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.nonExhaustingBallot)

    assert(tallyFor(SenateElectionTalliers.BallotTallier.ExhaustedVotes)(ballotWithFacts) === 0)
  }

}
