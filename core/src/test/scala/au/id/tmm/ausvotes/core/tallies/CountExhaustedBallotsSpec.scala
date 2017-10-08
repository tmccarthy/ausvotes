package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.fixtures.{BallotFixture, TestsBallotFacts}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountExhaustedBallotsSpec extends ImprovedFlatSpec with TestsBallotFacts {

  val sut = BallotCounter.ExhaustedBallots

  "the exhausted ballots count" should "count any exhausted ballot" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.exhaustingBallot)

    assert(sut.isCounted(ballotWithFacts) === true)
  }

  it should "not count a ballot that is not exhausting" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.nonExhaustingBallot)

    assert(sut.isCounted(ballotWithFacts) === false)
  }

}
