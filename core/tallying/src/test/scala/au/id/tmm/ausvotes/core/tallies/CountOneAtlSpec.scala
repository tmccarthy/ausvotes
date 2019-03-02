package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.fixtures.BallotFixture
import au.id.tmm.ausvotes.core.tallies.SenateTalliesUtils._
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountOneAtlSpec extends ImprovedFlatSpec {

  import au.id.tmm.ausvotes.core.computations.BallotFactsTestUtils.ACT._

  "the one atl talliers" should "count ballots with only 1 marked above the line" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.oneAtl)

    assert(isCounted(SenateElectionTalliers.BallotTallier.Voted1Atl)(ballotWithFacts) === true)
  }

  they should "not count ballots marked with a 1 atl and preferences expressed btl" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.oneAtlFormalBtl)

    assert(isCounted(SenateElectionTalliers.BallotTallier.Voted1Atl)(ballotWithFacts) === false)
  }

  they should "not count ballots marked with more than one preference atl" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.formalAtl)

    assert(isCounted(SenateElectionTalliers.BallotTallier.Voted1Atl)(ballotWithFacts) === false)
  }

  they should "count ballots marked only with a tick atl" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.oneTickAtl)

    assert(isCounted(SenateElectionTalliers.BallotTallier.Voted1Atl)(ballotWithFacts) === true)
  }

  they should "count ballots marked only with a cross atl" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.oneCrossAtl)

    assert(isCounted(SenateElectionTalliers.BallotTallier.Voted1Atl)(ballotWithFacts) === true)
  }
}
