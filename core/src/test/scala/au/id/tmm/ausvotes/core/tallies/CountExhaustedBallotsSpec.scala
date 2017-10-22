package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.computations.BallotWithFacts
import au.id.tmm.ausvotes.core.fixtures.{BallotFactsTestUtils, BallotFixture}
import au.id.tmm.ausvotes.core.model.computation.{BallotExhaustion, FirstPreference}
import au.id.tmm.ausvotes.core.model.parsing.Party
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountExhaustedBallotsSpec extends ImprovedFlatSpec {

  import BallotFactsTestUtils.ACT._

  val sut = BallotCounter.ExhaustedBallots

  "the exhausted ballots count" should "count any exhausted ballot" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.exhaustingBallot)

    assert(sut.isCounted(ballotWithFacts) === true)
  }

  it should "not count a ballot that is not exhausting" in {
    val ballotWithFacts = factsFor(BallotFixture.ACT.nonExhaustingBallot)

    assert(sut.isCounted(ballotWithFacts) === false)
  }

  it should "count a ballot that was exhausted before the initial count" in {
    import BallotFactsTestUtils.WA._

    val ballot = BallotFixture.WA.onlyPreferencesIneligible
    val normalisedBallot = normaliser.normalise(ballot)
    val firstPreference = FirstPreference(ballot.btlPreferences.head._1.group, Party.Independent)

    val ballotWithFacts = BallotWithFacts(
      ballot = ballot,
      normalisedBallot = normalisedBallot,
      isDonkeyVote = false,
      firstPreference = firstPreference,
      matchingHowToVote = None,
      exhaustion = BallotExhaustion.ExhaustedBeforeInitialAllocation,
      savingsProvisionsUsed = Set(),
    )

    assert(sut.isCounted(ballotWithFacts) === true)
  }
}
