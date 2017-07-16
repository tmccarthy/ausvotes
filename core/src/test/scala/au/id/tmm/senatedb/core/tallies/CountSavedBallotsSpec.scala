package au.id.tmm.senatedb.core.tallies

import au.id.tmm.senatedb.core.fixtures.{Ballots, TestsBallotFacts}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountSavedBallotsSpec extends ImprovedFlatSpec with TestsBallotFacts {

  "the saved ballots counter" should "count ballots used savings provisions" in {
    val ballotWithFacts = factsFor(Ballots.ACT.tickedAtl)

    assert(CountSavedBallots.shouldCount(ballotWithFacts) === true)
  }

  it should "not count a normal ballot" in {
    val ballotWithFacts = factsFor(Ballots.ACT.formalAtl)

    assert(CountSavedBallots.shouldCount(ballotWithFacts) === false)
  }
}
