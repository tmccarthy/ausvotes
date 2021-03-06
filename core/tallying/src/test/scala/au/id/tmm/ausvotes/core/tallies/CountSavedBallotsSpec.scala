package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.fixtures.BallotFixture
import au.id.tmm.ausvotes.core.tallies.SenateTalliesUtils._
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountSavedBallotsSpec extends ImprovedFlatSpec {

  import au.id.tmm.ausvotes.core.computations.BallotFactsTestUtils.ACT._

  "the saved ballots counter" should "count ballots used savings provisions" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.tickedAtl)

    assert(isCounted(SenateElectionTalliers.BallotTallier.UsedSavingsProvision)(ballotWithFacts) === true)
  }

  it should "not count a normal ballot" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.formalAtl)

    assert(isCounted(SenateElectionTalliers.BallotTallier.UsedSavingsProvision)(ballotWithFacts) === false)
  }
}
