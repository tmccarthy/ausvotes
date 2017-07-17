package au.id.tmm.senatedb.core.tallies

import au.id.tmm.senatedb.core.fixtures.{Ballots, TestsBallotFacts}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountExhaustedVotesSpec extends ImprovedFlatSpec with TestsBallotFacts {

  val sut = BallotCounter.ExhaustedVotes

  "the exhausted ballots count" should "count any exhausted ballot" in {
    val ballotWithFacts = factsFor(Ballots.ACT.exhaustingBallot)

    assert(sut.weigh(Seq(ballotWithFacts)) === 0.113066455002141d)
  }

  it should "not count a ballot that is not exhausting" in {
    val ballotWithFacts = factsFor(Ballots.ACT.nonExhaustingBallot)

    assert(sut.weigh(Seq(ballotWithFacts)) === 0)
  }
}
