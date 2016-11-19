package au.id.tmm.senatedb.tallies

import au.id.tmm.senatedb.fixtures.{Ballots, TestsBallotFacts}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountOneAtlSpec extends ImprovedFlatSpec with TestsBallotFacts {

  "the one atl talliers" should "count ballots with only 1 marked above the line" in {
    val ballotWithFacts = factsFor(Ballots.ACT.oneAtl)

    assert(CountOneAtl.shouldCount(ballotWithFacts) === true)
  }

  they should "not count ballots marked with a 1 atl and preferences expressed btl" in {
    val ballotWithFacts = factsFor(Ballots.ACT.oneAtlFormalBtl)

    assert(CountOneAtl.shouldCount(ballotWithFacts) === false)
  }

  they should "not count ballots marked with more than one preference atl" in {
    val ballotWithFacts = factsFor(Ballots.ACT.formalAtl)

    assert(CountOneAtl.shouldCount(ballotWithFacts) === false)
  }

  they should "count ballots marked only with a tick atl" in {
    val ballotWithFacts = factsFor(Ballots.ACT.oneTickAtl)

    assert(CountOneAtl.shouldCount(ballotWithFacts) === true)
  }

  they should "count ballots marked only with a cross atl" in {
    val ballotWithFacts = factsFor(Ballots.ACT.oneCrossAtl)

    assert(CountOneAtl.shouldCount(ballotWithFacts) === true)
  }
}
