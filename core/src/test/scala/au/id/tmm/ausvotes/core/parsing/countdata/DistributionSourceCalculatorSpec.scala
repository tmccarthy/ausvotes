package au.id.tmm.ausvotes.core.parsing.countdata

import au.id.tmm.ausvotes.core.fixtures.{BallotFixture, CandidateFixture, CountDataTestUtils}
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.Party.Independent
import au.id.tmm.ausvotes.core.model.parsing.{Candidate, Name}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class DistributionSourceCalculatorSpec extends ImprovedFlatSpec {

  import BallotFixture.ACT.ballotMaker.candidatePosition
  import CountDataTestUtils.ACT._

  private val sut = new DistributionSourceCalculator(CandidateFixture.ACT.candidates)

  "a distribution source calculator" should "fail if the comment mentions an unknown candidate" in {
    val comment = "CRANIUM ,R has 176559 surplus vote(s) to be distributed in count # 5 at a transfer value of " +
      "0.520945945945945. 338920 papers are involved from count number(s) 1,2."

    intercept[IllegalStateException](sut.calculateFor(comment, countData.steps.take(4)))
  }

  it should "fail if the comment says a candidate is excluded when none have been excluded" in {
    val comment = "Preferences with a transfer value of 1 will be distributed in count # 2 after the exclusion " +
      "of 1 candidate(s). Preferences received at count(s) 1."

    intercept[IllegalStateException](sut.calculateFor(comment, countData.steps.take(1)))
  }

  it should "fail if the comment says more than one candidate was excluded" in {
    val comment = "Preferences with a transfer value of 1 will be distributed in count # 2 after the exclusion " +
      "of 2 candidate(s). Preferences received at count(s) 1."

    intercept[UnsupportedOperationException](sut.calculateFor(comment, countData.steps.take(1)))
  }

  it should "pick up the elected candidate if two candidates have the same name as the candidate listed in an election comment" in {
    // There's already a Katy GALLAGHER
    val candidates = CandidateFixture.ACT.candidates +
      Candidate(SenateElection.`2016`, State.ACT, "42", Name("Keith", "GALLAGHER"), Independent, candidatePosition("UG2"))

    val sut = new DistributionSourceCalculator(candidates)

    val comment = "GALLAGHER ,K has 10826 surplus vote(s) to be distributed in count # 2 at a transfer value of " +
      "0.113066455002141. 95749 papers are involved from count number(s) 1."

    val distributionSource = sut.calculateFor(comment, countData.steps.take(1))

    assert(distributionSource.get.sourceCandidate ===
      CandidateFixture.ACT.candidateWithName(Name("Katy", "GALLAGHER")).btlPosition)
  }

  it should "not produce a source if the candidate was elected as the last woman standing" in {
    val comment = "SESELJA, Z, have been elected to the remaining positions."

    assert(None === sut.calculateFor(comment, countData.steps.take(1)))
  }
}
