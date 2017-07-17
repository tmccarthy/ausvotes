package au.id.tmm.senatedb.core.tallies

import au.id.tmm.senatedb.core.fixtures.{Ballots, TestsBallotFacts}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountOneAtlSpec extends ImprovedFlatSpec with TestsBallotFacts {

  val sut = BallotCounter.Voted1Atl

  "the one atl talliers" should "count ballots with only 1 marked above the line" in {
    val ballotWithFacts = factsFor(Ballots.ACT.oneAtl)

    assert(sut.isCounted(ballotWithFacts) === true)
  }

  they should "not count ballots marked with a 1 atl and preferences expressed btl" in {
    val ballotWithFacts = factsFor(Ballots.ACT.oneAtlFormalBtl)

    assert(sut.isCounted(ballotWithFacts) === false)
  }

  they should "not count ballots marked with more than one preference atl" in {
    val ballotWithFacts = factsFor(Ballots.ACT.formalAtl)

    assert(sut.isCounted(ballotWithFacts) === false)
  }

  they should "count ballots marked only with a tick atl" in {
    val ballotWithFacts = factsFor(Ballots.ACT.oneTickAtl)

    assert(sut.isCounted(ballotWithFacts) === true)
  }

  they should "count ballots marked only with a cross atl" in {
    val ballotWithFacts = factsFor(Ballots.ACT.oneCrossAtl)

    assert(sut.isCounted(ballotWithFacts) === true)
  }
}
