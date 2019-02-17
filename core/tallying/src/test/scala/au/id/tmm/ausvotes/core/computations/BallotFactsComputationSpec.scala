package au.id.tmm.ausvotes.core.computations

import au.id.tmm.ausvotes.core.fixtures._
import au.id.tmm.ausvotes.core.model.computation.{BallotExhaustion, FirstPreference}
import au.id.tmm.ausvotes.model.Party
import au.id.tmm.ausvotes.model.federal.senate.NormalisedSenateBallot
import au.id.tmm.countstv.normalisation.{BallotNormalisation, BallotNormalisationRule}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class BallotFactsComputationSpec extends ImprovedFlatSpec {
  import BallotFactsTestUtils.ACT._

  private val ballotMaker = BallotMaker(CandidateFixture.ACT)

  private val testBallot = BallotFixture.ACT.formalAtl

  "ballot facts computation" should "correctly match the original ballot to its facts" in {
    val ballotWithFacts = factsFor(testBallot)

    assert(ballotWithFacts.ballot eq testBallot)
  }

  it should "compute the normalised ballot" in {
    val ballotWithFacts = factsFor(testBallot)

    val expectedNormalisedAtl =
      ballotMaker.candidateOrder("A0", "A1", "B0", "B1", "C0", "C1", "D0", "D1", "E0", "E1", "F0", "F1")

    val expectedNormalisedBallot = NormalisedSenateBallot(
      atl = BallotNormalisation.Result.Formal(ballotMaker.groupOrder("A", "B", "C", "D", "E", "F")),
      atlCandidateOrder = Some(expectedNormalisedAtl),
      btl = BallotNormalisation.Result.Informal(
        normalisedBallot = Vector.empty,
        optionalRulesViolated = Set(BallotNormalisationRule.MinimumPreferences(12)),
        mandatoryRulesViolated = Set(BallotNormalisationRule.MinimumPreferences(6)),
      ),
      canonicalOrder = Some(expectedNormalisedAtl),
    )

    assert(ballotWithFacts.normalisedBallot === expectedNormalisedBallot)
  }

  it should "compute whether the ballot is a donkey vote" in {
    val ballotWithFacts = factsFor(testBallot)

    assert(ballotWithFacts.isDonkeyVote)
  }

  it should "compute the first preferenced party" in {
    val ballotWithFacts = factsFor(testBallot)

    assert(ballotWithFacts.firstPreference ===
      FirstPreference(ballotMaker.group("A"), Some(Party("Liberal Democratic Party"))))
  }

  it should "compute the exhaustion" in {
    val ballotWithFacts = factsFor(testBallot)

    assert(ballotWithFacts.exhaustion === BallotExhaustion.NotExhausted)
  }
}
