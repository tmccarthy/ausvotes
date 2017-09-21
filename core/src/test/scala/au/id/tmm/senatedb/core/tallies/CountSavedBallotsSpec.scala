package au.id.tmm.senatedb.core.tallies

import au.id.tmm.senatedb.core.fixtures.{BallotFixture, TestsBallotFacts}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountSavedBallotsSpec extends ImprovedFlatSpec with TestsBallotFacts {

  val sut = BallotCounter.UsedSavingsProvision

  "the saved ballots counter" should "count ballots used savings provisions" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.tickedAtl)

    assert(sut.isCounted(ballotWithFacts) === true)
  }

  it should "not count a normal ballot" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.formalAtl)

    assert(sut.isCounted(ballotWithFacts) === false)
  }
}
