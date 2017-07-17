package au.id.tmm.senatedb.core.tallies

import au.id.tmm.senatedb.core.fixtures.{Ballots, TestsBallotFacts}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountExhaustedBallotsSpec extends ImprovedFlatSpec with TestsBallotFacts {

  val sut = BallotCounter.ExhaustedBallots

  "the exhausted ballots count" should "count any exhausted ballot" in {
    val ballotWithFacts = factsFor(Ballots.ACT.exhaustingBallot)

    assert(sut.isCounted(ballotWithFacts) === true)
  }

  it should "not count a ballot that is not exhausting" in {
    val ballotWithFacts = factsFor(Ballots.ACT.nonExhaustingBallot)

    assert(sut.isCounted(ballotWithFacts) === false)
  }

}
