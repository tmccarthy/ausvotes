package au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.senate_count_data

import au.id.tmm.ausvotes.core.fixtures.{BallotFixture, CandidateFixture}
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.ausvotes.model.{CandidateDetails, Name}
import au.id.tmm.countstv.model.values.Count
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class DistributionSourceCalculatorSpec extends ImprovedFlatSpec {

  import BallotFixture.ACT.ballotMaker.candidatePosition
  import CountDataTestUtils.ACT._

  private val sut = new DistributionSourceCalculator(CandidateFixture.ACT.candidates)

  "a distribution source calculator" should "fail if the comment mentions an unknown candidate" in {
    val comment = "CRANIUM ,R has 176559 surplus vote(s) to be distributed in count # 5 at a transfer value of " +
      "0.520945945945945. 338920 papers are involved from count number(s) 1,2."

    assert(sut.calculateFor(comment, actualCountData.completedCount.countSteps.truncateAfter(Count(4))).left.map(_.getClass) === Left(classOf[IllegalStateException]))
  }

  it should "fail if the comment says a candidate is excluded when none have been excluded" in {
    val comment = "Preferences with a transfer value of 1 will be distributed in count # 2 after the exclusion " +
      "of 1 candidate(s). Preferences received at count(s) 1."

    assert(sut.calculateFor(comment, actualCountData.completedCount.countSteps.truncateAfter(Count(1))).left.map(_.getClass) === Left(classOf[IllegalStateException]))
  }

  it should "fail if the comment says more than one candidate was excluded" in {
    val comment = "Preferences with a transfer value of 1 will be distributed in count # 2 after the exclusion " +
      "of 2 candidate(s). Preferences received at count(s) 1."

    assert(sut.calculateFor(comment, actualCountData.completedCount.countSteps.truncateAfter(Count(1))).left.map(_.getClass) === Left(classOf[UnsupportedOperationException]))
  }

  it should "pick up the elected candidate if two candidates have the same name as the candidate listed in an election comment" in {
    // There's already a Katy GALLAGHER
    val candidates = CandidateFixture.ACT.candidates +
      SenateCandidate(
        CandidateFixture.ACT.election,
        SenateCandidateDetails(
          CandidateFixture.ACT.election,
          Name("Keith", "GALLAGHER"),
          party = None,
          CandidateDetails.Id(42),
        ),
        candidatePosition("UG2"),
      )

    val sut = new DistributionSourceCalculator(candidates)

    val comment = "GALLAGHER ,K has 10826 surplus vote(s) to be distributed in count # 2 at a transfer value of " +
      "0.113066455002141. 95749 papers are involved from count number(s) 1."

    val distributionSource = sut.calculateFor(comment, actualCountData.completedCount.countSteps.truncateAfter(Count(1)))

    assert(distributionSource.right.get.get.candidate ===
      CandidateFixture.ACT.candidateWithName(Name("Katy", "GALLAGHER")))
  }

  it should "not produce a source if the candidate was elected as the last woman standing" in {
    val comment = "SESELJA, Z, have been elected to the remaining positions."

    assert(sut.calculateFor(comment, actualCountData.completedCount.countSteps.truncateAfter(Count(1))) === Right(None))
  }
}
