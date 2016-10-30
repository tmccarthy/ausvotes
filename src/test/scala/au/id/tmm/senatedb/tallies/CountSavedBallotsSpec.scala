package au.id.tmm.senatedb.tallies

import au.id.tmm.senatedb.fixtures.{Ballots, TestsBallotFacts}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountSavedBallotsSpec extends ImprovedFlatSpec with TestsBallotFacts {

  "the saved ballots counter" should "count ballots that use ticks" in {
    val ballotWithFacts = factsFor(Ballots.ACT.tickedAtl)

    assert(CountSavedBallots.shouldCount(ballotWithFacts) === true)
  }

  it should "count ballots that use crosses" in {
    val ballotWithFacts = factsFor(Ballots.ACT.crossedAtl)

    assert(CountSavedBallots.shouldCount(ballotWithFacts) === true)
  }

  it should "count a ballot with a repeated number ATL" in {
    val ballotWithFacts = factsFor(Ballots.ACT.atlWithRepeatedNumbers)

    assert(CountSavedBallots.shouldCount(ballotWithFacts) === true)
  }

  it should "count a ballot with a repeated number BTL after 6" in {
    val ballotWithFacts = factsFor(Ballots.ACT.btlRepeatedNumberAfter6)

    assert(CountSavedBallots.shouldCount(ballotWithFacts) === true)
  }

  it should "count a ballot with a missed number ATL" in {
    val ballotWithFacts = factsFor(Ballots.ACT.atlMissedNumbers)

    assert(CountSavedBallots.shouldCount(ballotWithFacts) === true)
  }

  it should "count a ballot with a missed number BTL" in {
    val ballotWithFacts = factsFor(Ballots.ACT.btlMissedNumberAfter6)

    assert(CountSavedBallots.shouldCount(ballotWithFacts) === true)
  }

  it should "count a ballot with at least one but less than 6 formal preferences above the line" in {
    val ballotWithFacts = factsFor(Ballots.ACT.oneAtl)

    assert(CountSavedBallots.shouldCount(ballotWithFacts) === true)
  }

  it should "count a ballot with at least 6 but less than 12 formal preferences below the line" in {
    val ballotWithFacts = factsFor(Ballots.ACT.sixNumberedBtl)

    assert(CountSavedBallots.shouldCount(ballotWithFacts) === true)
  }

  it should "not count a normal ballot atl" in {
    val ballotWithFacts = factsFor(Ballots.ACT.formalAtl)

    assert(CountSavedBallots.shouldCount(ballotWithFacts) === false)
  }

  it should "not count a normal ballot btl" in {
    val ballotWithFacts = factsFor(Ballots.ACT.formalBtl)

    assert(CountSavedBallots.shouldCount(ballotWithFacts) === false)
  }
}
