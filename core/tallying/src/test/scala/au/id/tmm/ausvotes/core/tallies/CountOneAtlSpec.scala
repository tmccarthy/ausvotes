package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.fixtures.BallotFixture
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountOneAtlSpec extends ImprovedFlatSpec {

  import au.id.tmm.ausvotes.core.computations.BallotFactsTestUtils.ACT._

  val sut = BallotCounter.Voted1Atl

  "the one atl talliers" should "count ballots with only 1 marked above the line" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.oneAtl)

    assert(sut.isCounted(ballotWithFacts) === true)
  }

  they should "not count ballots marked with a 1 atl and preferences expressed btl" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.oneAtlFormalBtl)

    assert(sut.isCounted(ballotWithFacts) === false)
  }

  they should "not count ballots marked with more than one preference atl" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.formalAtl)

    assert(sut.isCounted(ballotWithFacts) === false)
  }

  they should "count ballots marked only with a tick atl" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.oneTickAtl)

    assert(sut.isCounted(ballotWithFacts) === true)
  }

  they should "count ballots marked only with a cross atl" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.oneCrossAtl)

    assert(sut.isCounted(ballotWithFacts) === true)
  }
}
