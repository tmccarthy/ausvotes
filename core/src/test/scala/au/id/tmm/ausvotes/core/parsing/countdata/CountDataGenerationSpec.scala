package au.id.tmm.ausvotes.core.parsing.countdata

import au.id.tmm.ausvotes.core.fixtures.{BallotFixture, CountDataTestUtils}
import au.id.tmm.ausvotes.core.model.parsing.CandidatePosition
import au.id.tmm.countstv.model.CandidateDistributionReason._
import au.id.tmm.countstv.model.CandidateStatus._
import au.id.tmm.countstv.model.countsteps.{AllocationAfterIneligibles, DistributionCountStep, InitialAllocation}
import au.id.tmm.countstv.model.values._
import au.id.tmm.countstv.model.{CandidateStatuses, CandidateVoteCounts, VoteCount}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountDataGenerationSpec extends ImprovedFlatSpec {

  import CountDataTestUtils.ACT._

  private val ballotMaker = BallotFixture.ACT.ballotMaker
  import ballotMaker.candidatePosition

  private val actualCountData = countData

  private val statusesAllRemaining =
    CandidateStatuses(groupsAndCandidates.candidates.map(_.btlPosition -> Remaining).toMap)

  "the generated count data" should "have the correct initial allocation" in {
    val expectedInitialAllocation = InitialAllocation[CandidatePosition](
      candidateStatuses = statusesAllRemaining,
      candidateVoteCounts = CandidateVoteCounts[CandidatePosition](
        perCandidate = Map(
          candidatePosition("A0") -> VoteCount(7371),
          candidatePosition("A1") -> VoteCount(89),
          candidatePosition("B0") -> VoteCount(1322),
          candidatePosition("B1") -> VoteCount(56),
          candidatePosition("C0") -> VoteCount(95749),
          candidatePosition("C1") -> VoteCount(918),
          candidatePosition("D0") -> VoteCount(2455),
          candidatePosition("D1") -> VoteCount(68),
          candidatePosition("E0") -> VoteCount(2557),
          candidatePosition("E1") -> VoteCount(121),
          candidatePosition("F0") -> VoteCount(82932),
          candidatePosition("F1") -> VoteCount(1683),
          candidatePosition("G0") -> VoteCount(4150),
          candidatePosition("G1") -> VoteCount(101),
          candidatePosition("H0") -> VoteCount(40424),
          candidatePosition("H1") -> VoteCount(582),
          candidatePosition("I0") -> VoteCount(3011),
          candidatePosition("I1") -> VoteCount(76),
          candidatePosition("J0") -> VoteCount(9744),
          candidatePosition("J1") -> VoteCount(352),
          candidatePosition("UG0") -> VoteCount(698),
          candidatePosition("UG1") -> VoteCount(308),
        ),
        exhausted = VoteCount.zero,
        roundingError = VoteCount.zero,
      )
    )

    assert(actualCountData.completedCount.countSteps.initialAllocation === expectedInitialAllocation)
  }

  it should "have the correct allocation after ineligibles" in {
    val expectedAllocationAfterIneligibles = AllocationAfterIneligibles[CandidatePosition](
      candidateStatuses = statusesAllRemaining.update(candidatePosition("C0"), Elected(Ordinal.first, Count(1))),
      candidateVoteCounts = CandidateVoteCounts[CandidatePosition](
        perCandidate = Map(
          candidatePosition("A0") -> VoteCount(7371),
          candidatePosition("A1") -> VoteCount(89),
          candidatePosition("B0") -> VoteCount(1322),
          candidatePosition("B1") -> VoteCount(56),
          candidatePosition("C0") -> VoteCount(95749),
          candidatePosition("C1") -> VoteCount(918),
          candidatePosition("D0") -> VoteCount(2455),
          candidatePosition("D1") -> VoteCount(68),
          candidatePosition("E0") -> VoteCount(2557),
          candidatePosition("E1") -> VoteCount(121),
          candidatePosition("F0") -> VoteCount(82932),
          candidatePosition("F1") -> VoteCount(1683),
          candidatePosition("G0") -> VoteCount(4150),
          candidatePosition("G1") -> VoteCount(101),
          candidatePosition("H0") -> VoteCount(40424),
          candidatePosition("H1") -> VoteCount(582),
          candidatePosition("I0") -> VoteCount(3011),
          candidatePosition("I1") -> VoteCount(76),
          candidatePosition("J0") -> VoteCount(9744),
          candidatePosition("J1") -> VoteCount(352),
          candidatePosition("UG0") -> VoteCount(698),
          candidatePosition("UG1") -> VoteCount(308),
        ),
        exhausted = VoteCount.zero,
        roundingError = VoteCount.zero,
      ),
      transfersDueToIneligibles = Map.empty,
    )

    assert(actualCountData.completedCount.countSteps(Count.ofIneligibleCandidateHandling) === expectedAllocationAfterIneligibles)
  }

  it should "have the correct number of formal ballots" in {
    assert(actualCountData.completedCount.numFormalPapers === NumPapers(254767))
  }

  it should "have the correct quota" in {
    assert(actualCountData.completedCount.quota === NumVotes(84923))
  }

  it should "have the correct 8th distribution step" in {

    val expectedDistributionStep = DistributionCountStep[CandidatePosition](
      Count(8),
      candidateStatuses = statusesAllRemaining.updateFrom(Map(
        candidatePosition("C0") -> Elected(Ordinal.first, Count(1)),
        candidatePosition("B1") -> Excluded(Ordinal.first, Count(2)),
        candidatePosition("D1") -> Excluded(Ordinal.second, Count(4)),
        candidatePosition("I1") -> Excluded(Ordinal.third, Count(6)),
        candidatePosition("A1") -> Excluded(Ordinal.fourth, Count(8)),
      )),
      candidateVoteCounts = CandidateVoteCounts(
        perCandidate = Map(
          candidatePosition("A0") -> VoteCount(NumPapers(7480),NumVotes(7385)),
          candidatePosition("A1") -> VoteCount(NumPapers(113),NumVotes(93)),
          candidatePosition("B0") -> VoteCount(NumPapers(1394),NumVotes(1354)),
          candidatePosition("B1") -> VoteCount(NumPapers(0),NumVotes(0)),
          candidatePosition("C0") -> VoteCount(NumPapers(0),NumVotes(84923)),
          candidatePosition("C1") -> VoteCount(NumPapers(92338),NumVotes(11262)),
          candidatePosition("D0") -> VoteCount(NumPapers(2615),NumVotes(2520)),
          candidatePosition("D1") -> VoteCount(NumPapers(0),NumVotes(0)),
          candidatePosition("E0") -> VoteCount(NumPapers(2702),NumVotes(2577)),
          candidatePosition("E1") -> VoteCount(NumPapers(152),NumVotes(130)),
          candidatePosition("F0") -> VoteCount(NumPapers(83551),NumVotes(83006)),
          candidatePosition("F1") -> VoteCount(NumPapers(1856),NumVotes(1705)),
          candidatePosition("G0") -> VoteCount(NumPapers(4246),NumVotes(4162)),
          candidatePosition("G1") -> VoteCount(NumPapers(128),NumVotes(106)),
          candidatePosition("H0") -> VoteCount(NumPapers(43033),NumVotes(40721)),
          candidatePosition("H1") -> VoteCount(NumPapers(697),NumVotes(595)),
          candidatePosition("I0") -> VoteCount(NumPapers(3107),NumVotes(3074)),
          candidatePosition("I1") -> VoteCount(NumPapers(0),NumVotes(0)),
          candidatePosition("J0") -> VoteCount(NumPapers(9903),NumVotes(9763)),
          candidatePosition("J1") -> VoteCount(NumPapers(393),NumVotes(360)),
          candidatePosition("UG0") -> VoteCount(NumPapers(727),NumVotes(704)),
          candidatePosition("UG1") -> VoteCount(NumPapers(332),NumVotes(314))
        ),
        exhausted = VoteCount(NumPapers(0), NumVotes(0)),
        roundingError = VoteCount(NumPapers(0), NumVotes(13))
      ),
      distributionSource = DistributionCountStep.Source(
        candidate = candidatePosition("I1"),
        candidateDistributionReason = Exclusion,
        sourceCounts = Set(Count(2)),
        transferValue = TransferValue(0.113066455002141d),
      )
    )

    assert(actualCountData.completedCount.countSteps(Count(8)) === expectedDistributionStep)
  }

  it should "have the correct last distribution step" in {

    val expectedDistributionStep = DistributionCountStep[CandidatePosition](
      Count(29),
      candidateStatuses = statusesAllRemaining.updateFrom(Map(
        candidatePosition("C0") -> Elected(Ordinal.first, Count(1)),
        candidatePosition("F0") -> Elected(Ordinal.second, Count(29)),
        candidatePosition("B1") -> Excluded(Ordinal(0), Count(2)),
        candidatePosition("D1") -> Excluded(Ordinal(1), Count(4)),
        candidatePosition("I1") -> Excluded(Ordinal(2), Count(6)),
        candidatePosition("A1") -> Excluded(Ordinal(3), Count(8)),
        candidatePosition("G1") -> Excluded(Ordinal(4), Count(10)),
        candidatePosition("E1") -> Excluded(Ordinal(5), Count(12)),
        candidatePosition("UG1") -> Excluded(Ordinal(6), Count(14)),
        candidatePosition("J1") -> Excluded(Ordinal(7), Count(16)),
        candidatePosition("H1") -> Excluded(Ordinal(8), Count(18)),
        candidatePosition("UG0") -> Excluded(Ordinal(9), Count(20)),
        candidatePosition("B0") -> Excluded(Ordinal(10), Count(22)),
        candidatePosition("F1") -> Excluded(Ordinal(11), Count(24)),
        candidatePosition("D0") -> Excluded(Ordinal(12), Count(26)),
        candidatePosition("E0") -> Excluded(Ordinal(13), Count(28)),
      )),
      candidateVoteCounts = CandidateVoteCounts(
        perCandidate = Map(
          candidatePosition("A0") -> VoteCount(NumPapers(8382), NumVotes(8251)),
          candidatePosition("A1") -> VoteCount(NumPapers(0), NumVotes(0)),
          candidatePosition("B0") -> VoteCount(NumPapers(0), NumVotes(0)),
          candidatePosition("B1") -> VoteCount(NumPapers(0), NumVotes(0)),
          candidatePosition("C0") -> VoteCount(NumPapers(0), NumVotes(84923)),
          candidatePosition("C1") -> VoteCount(NumPapers(93804), NumVotes(12593)),
          candidatePosition("D0") -> VoteCount(NumPapers(0), NumVotes(0)),
          candidatePosition("D1") -> VoteCount(NumPapers(0), NumVotes(0)),
          candidatePosition("E0") -> VoteCount(NumPapers(206), NumVotes(18)),
          candidatePosition("E1") -> VoteCount(NumPapers(0), NumVotes(0)),
          candidatePosition("F0") -> VoteCount(NumPapers(85600), NumVotes(85000)),
          candidatePosition("F1") -> VoteCount(NumPapers(0), NumVotes(0)),
          candidatePosition("G0") -> VoteCount(NumPapers(5557), NumVotes(5419)),
          candidatePosition("G1") -> VoteCount(NumPapers(0), NumVotes(0)),
          candidatePosition("H0") -> VoteCount(NumPapers(45134), NumVotes(42682)),
          candidatePosition("H1") -> VoteCount(NumPapers(0), NumVotes(0)),
          candidatePosition("I0") -> VoteCount(NumPapers(3929), NumVotes(3883)),
          candidatePosition("I1") -> VoteCount(NumPapers(0), NumVotes(0)),
          candidatePosition("J0") -> VoteCount(NumPapers(12046), NumVotes(11857)),
          candidatePosition("J1") -> VoteCount(NumPapers(0), NumVotes(0)),
          candidatePosition("UG0") -> VoteCount(NumPapers(0), NumVotes(0)),
          candidatePosition("UG1") -> VoteCount(NumPapers(0), NumVotes(0)),
        ),
        exhausted = VoteCount(NumPapers(109), NumVotes(109)),
        roundingError = VoteCount(NumPapers(0), NumVotes(32)),
      ),
      distributionSource = DistributionCountStep.Source(
        candidate = candidatePosition("E0"),
        candidateDistributionReason = Exclusion,
        sourceCounts = Set(Count(1), Count(5), Count(7), Count(9), Count(11), Count(13), Count(15), Count(17), Count(19), Count(21), Count(23), Count(25), Count(27)),
        transferValue = TransferValue(1d),
      )
    )

    assert(actualCountData.completedCount.countSteps(Count(29)) === expectedDistributionStep)
  }

  it should "have the correct candidate outcomes" in {
    val expectedOutcomes = CandidateStatuses(
      candidatePosition("A0") -> Remaining,
      candidatePosition("A1") -> Excluded(Ordinal(4 - 1), Count(8)),
      candidatePosition("B0") -> Excluded(Ordinal(11 - 1), Count(22)),
      candidatePosition("B1") -> Excluded(Ordinal(1 - 1), Count(2)),
      candidatePosition("C0") -> Elected(Ordinal.first, Count(1)),
      candidatePosition("C1") -> Remaining,
      candidatePosition("D0") -> Excluded(Ordinal(13 - 1), Count(26)),
      candidatePosition("D1") -> Excluded(Ordinal(2 - 1), Count(4)),
      candidatePosition("E0") -> Excluded(Ordinal(14 - 1), Count(28)),
      candidatePosition("E1") -> Excluded(Ordinal(6 - 1), Count(12)),
      candidatePosition("F0") -> Elected(Ordinal.second, Count(29)),
      candidatePosition("F1") -> Excluded(Ordinal(12 - 1), Count(24)),
      candidatePosition("G0") -> Remaining,
      candidatePosition("G1") -> Excluded(Ordinal(5 - 1), Count(10)),
      candidatePosition("H0") -> Remaining,
      candidatePosition("H1") -> Excluded(Ordinal(9 - 1), Count(18)),
      candidatePosition("I0") -> Remaining,
      candidatePosition("I1") -> Excluded(Ordinal(3 - 1), Count(6)),
      candidatePosition("J0") -> Remaining,
      candidatePosition("J1") -> Excluded(Ordinal(8 - 1), Count(16)),
      candidatePosition("UG0") -> Excluded(Ordinal(10 - 1), Count(20)),
      candidatePosition("UG1") -> Excluded(Ordinal(7 - 1), Count(14))
    )

    assert(actualCountData.outcomes === expectedOutcomes)
  }
}
