package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.fixtures.BallotFixture
import au.id.tmm.ausvotes.core.tallies.SenateTalliesUtils._
import org.scalatest.FlatSpec

class CountExhaustedBallotsSpec extends FlatSpec {

  import au.id.tmm.ausvotes.core.computations.BallotFactsTestUtils.ACT._

  "the exhausted ballots count" should "count any exhausted ballot" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.exhaustingBallot)

    assert(isCounted(SenateElectionTalliers.BallotTallier.ExhaustedBallots)(ballotWithFacts) === true)
  }

  it should "not count a ballot that is not exhausting" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.nonExhaustingBallot)

    assert(isCounted(SenateElectionTalliers.BallotTallier.ExhaustedBallots)(ballotWithFacts) === false)
  }

}
