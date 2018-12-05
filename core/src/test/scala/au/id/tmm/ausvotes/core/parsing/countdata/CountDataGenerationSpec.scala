package au.id.tmm.ausvotes.core.parsing.countdata

import au.id.tmm.ausvotes.core.fixtures.{BallotFixture, CountDataTestUtils}
import au.id.tmm.ausvotes.core.model.parsing.Candidate
import au.id.tmm.countstv.model.CandidateDistributionReason._
import au.id.tmm.countstv.model.CandidateStatus._
import au.id.tmm.countstv.model.countsteps.{AllocationAfterIneligibles, DistributionCountStep, InitialAllocation}
import au.id.tmm.countstv.model.values._
import au.id.tmm.countstv.model.{CandidateStatuses, CandidateVoteCounts, VoteCount}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountDataGenerationSpec extends ImprovedFlatSpec {

  import CountDataTestUtils.ACT._

  private val ballotMaker = BallotFixture.ACT.ballotMaker
  import ballotMaker.candidateWithPosition

  private val actualCountData = countData

  private val statusesAllRemaining =
    CandidateStatuses(groupsAndCandidates.candidates.map(_ -> Remaining).toMap)

  "the generated count data" should "have the correct initial allocation" in {
    val expectedInitialAllocation = InitialAllocation[Candidate](
      candidateStatuses = statusesAllRemaining,
      candidateVoteCounts = CandidateVoteCounts[Candidate](
        perCandidate = Map(
          candidateWithPosition("A0") -> VoteCount(7371),
          candidateWithPosition("A1") -> VoteCount(89),
          candidateWithPosition("B0") -> VoteCount(1322),
          candidateWithPosition("B1") -> VoteCount(56),
          candidateWithPosition("C0") -> VoteCount(95749),
          candidateWithPosition("C1") -> VoteCount(918),
          candidateWithPosition("D0") -> VoteCount(2455),
          candidateWithPosition("D1") -> VoteCount(68),
          candidateWithPosition("E0") -> VoteCount(2557),
          candidateWithPosition("E1") -> VoteCount(121),
          candidateWithPosition("F0") -> VoteCount(82932),
          candidateWithPosition("F1") -> VoteCount(1683),
          candidateWithPosition("G0") -> VoteCount(4150),
          candidateWithPosition("G1") -> VoteCount(101),
          candidateWithPosition("H0") -> VoteCount(40424),
          candidateWithPosition("H1") -> VoteCount(582),
          candidateWithPosition("I0") -> VoteCount(3011),
          candidateWithPosition("I1") -> VoteCount(76),
          candidateWithPosition("J0") -> VoteCount(9744),
          candidateWithPosition("J1") -> VoteCount(352),
          candidateWithPosition("UG0") -> VoteCount(698),
          candidateWithPosition("UG1") -> VoteCount(308),
        ),
        exhausted = VoteCount.zero,
        roundingError = VoteCount.zero,
      )
    )

    assert(actualCountData.completedCount.countSteps.initialAllocation === expectedInitialAllocation)
  }

  it should "have the correct allocation after ineligibles" in {
    val expectedAllocationAfterIneligibles = AllocationAfterIneligibles[Candidate](
      candidateStatuses = statusesAllRemaining.update(candidateWithPosition("C0"), Elected(Ordinal.first, Count(1))),
      candidateVoteCounts = CandidateVoteCounts[Candidate](
        perCandidate = Map(
          candidateWithPosition("A0") -> VoteCount(7371),
          candidateWithPosition("A1") -> VoteCount(89),
          candidateWithPosition("B0") -> VoteCount(1322),
          candidateWithPosition("B1") -> VoteCount(56),
          candidateWithPosition("C0") -> VoteCount(95749),
          candidateWithPosition("C1") -> VoteCount(918),
          candidateWithPosition("D0") -> VoteCount(2455),
          candidateWithPosition("D1") -> VoteCount(68),
          candidateWithPosition("E0") -> VoteCount(2557),
          candidateWithPosition("E1") -> VoteCount(121),
          candidateWithPosition("F0") -> VoteCount(82932),
          candidateWithPosition("F1") -> VoteCount(1683),
          candidateWithPosition("G0") -> VoteCount(4150),
          candidateWithPosition("G1") -> VoteCount(101),
          candidateWithPosition("H0") -> VoteCount(40424),
          candidateWithPosition("H1") -> VoteCount(582),
          candidateWithPosition("I0") -> VoteCount(3011),
          candidateWithPosition("I1") -> VoteCount(76),
          candidateWithPosition("J0") -> VoteCount(9744),
          candidateWithPosition("J1") -> VoteCount(352),
          candidateWithPosition("UG0") -> VoteCount(698),
          candidateWithPosition("UG1") -> VoteCount(308),
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

    val expectedDistributionStep = DistributionCountStep[Candidate](
      Count(8),
      candidateStatuses = statusesAllRemaining.updateFrom(Map(
        candidateWithPosition("C0") -> Elected(Ordinal.first, Count(1)),
        candidateWithPosition("B1") -> Excluded(Ordinal.first, Count(2)),
        candidateWithPosition("D1") -> Excluded(Ordinal.second, Count(4)),
        candidateWithPosition("I1") -> Excluded(Ordinal.third, Count(6)),
        candidateWithPosition("A1") -> Excluded(Ordinal.fourth, Count(8)),
      )),
      candidateVoteCounts = CandidateVoteCounts(
        perCandidate = Map(
          candidateWithPosition("A0") -> VoteCount(NumPapers(7480),NumVotes(7385)),
          candidateWithPosition("A1") -> VoteCount(NumPapers(113),NumVotes(93)),
          candidateWithPosition("B0") -> VoteCount(NumPapers(1394),NumVotes(1354)),
          candidateWithPosition("B1") -> VoteCount(NumPapers(0),NumVotes(0)),
          candidateWithPosition("C0") -> VoteCount(NumPapers(0),NumVotes(84923)),
          candidateWithPosition("C1") -> VoteCount(NumPapers(92338),NumVotes(11262)),
          candidateWithPosition("D0") -> VoteCount(NumPapers(2615),NumVotes(2520)),
          candidateWithPosition("D1") -> VoteCount(NumPapers(0),NumVotes(0)),
          candidateWithPosition("E0") -> VoteCount(NumPapers(2702),NumVotes(2577)),
          candidateWithPosition("E1") -> VoteCount(NumPapers(152),NumVotes(130)),
          candidateWithPosition("F0") -> VoteCount(NumPapers(83551),NumVotes(83006)),
          candidateWithPosition("F1") -> VoteCount(NumPapers(1856),NumVotes(1705)),
          candidateWithPosition("G0") -> VoteCount(NumPapers(4246),NumVotes(4162)),
          candidateWithPosition("G1") -> VoteCount(NumPapers(128),NumVotes(106)),
          candidateWithPosition("H0") -> VoteCount(NumPapers(43033),NumVotes(40721)),
          candidateWithPosition("H1") -> VoteCount(NumPapers(697),NumVotes(595)),
          candidateWithPosition("I0") -> VoteCount(NumPapers(3107),NumVotes(3074)),
          candidateWithPosition("I1") -> VoteCount(NumPapers(0),NumVotes(0)),
          candidateWithPosition("J0") -> VoteCount(NumPapers(9903),NumVotes(9763)),
          candidateWithPosition("J1") -> VoteCount(NumPapers(393),NumVotes(360)),
          candidateWithPosition("UG0") -> VoteCount(NumPapers(727),NumVotes(704)),
          candidateWithPosition("UG1") -> VoteCount(NumPapers(332),NumVotes(314))
        ),
        exhausted = VoteCount(NumPapers(0), NumVotes(0)),
        roundingError = VoteCount(NumPapers(0), NumVotes(13))
      ),
      distributionSource = DistributionCountStep.Source(
        candidate = candidateWithPosition("I1"),
        candidateDistributionReason = Exclusion,
        sourceCounts = Set(Count(2)),
        transferValue = TransferValue(0.113066455002141d),
      )
    )

    assert(actualCountData.completedCount.countSteps(Count(8)) === expectedDistributionStep)
  }

  it should "have the correct last distribution step" in {

    val expectedDistributionStep = DistributionCountStep[Candidate](
      Count(29),
      candidateStatuses = statusesAllRemaining.updateFrom(Map(
        candidateWithPosition("C0") -> Elected(Ordinal.first, Count(1)),
        candidateWithPosition("F0") -> Elected(Ordinal.second, Count(29)),
        candidateWithPosition("B1") -> Excluded(Ordinal(0), Count(2)),
        candidateWithPosition("D1") -> Excluded(Ordinal(1), Count(4)),
        candidateWithPosition("I1") -> Excluded(Ordinal(2), Count(6)),
        candidateWithPosition("A1") -> Excluded(Ordinal(3), Count(8)),
        candidateWithPosition("G1") -> Excluded(Ordinal(4), Count(10)),
        candidateWithPosition("E1") -> Excluded(Ordinal(5), Count(12)),
        candidateWithPosition("UG1") -> Excluded(Ordinal(6), Count(14)),
        candidateWithPosition("J1") -> Excluded(Ordinal(7), Count(16)),
        candidateWithPosition("H1") -> Excluded(Ordinal(8), Count(18)),
        candidateWithPosition("UG0") -> Excluded(Ordinal(9), Count(20)),
        candidateWithPosition("B0") -> Excluded(Ordinal(10), Count(22)),
        candidateWithPosition("F1") -> Excluded(Ordinal(11), Count(24)),
        candidateWithPosition("D0") -> Excluded(Ordinal(12), Count(26)),
        candidateWithPosition("E0") -> Excluded(Ordinal(13), Count(28)),
      )),
      candidateVoteCounts = CandidateVoteCounts(
        perCandidate = Map(
          candidateWithPosition("A0") -> VoteCount(NumPapers(8382), NumVotes(8251)),
          candidateWithPosition("A1") -> VoteCount(NumPapers(0), NumVotes(0)),
          candidateWithPosition("B0") -> VoteCount(NumPapers(0), NumVotes(0)),
          candidateWithPosition("B1") -> VoteCount(NumPapers(0), NumVotes(0)),
          candidateWithPosition("C0") -> VoteCount(NumPapers(0), NumVotes(84923)),
          candidateWithPosition("C1") -> VoteCount(NumPapers(93804), NumVotes(12593)),
          candidateWithPosition("D0") -> VoteCount(NumPapers(0), NumVotes(0)),
          candidateWithPosition("D1") -> VoteCount(NumPapers(0), NumVotes(0)),
          candidateWithPosition("E0") -> VoteCount(NumPapers(206), NumVotes(18)),
          candidateWithPosition("E1") -> VoteCount(NumPapers(0), NumVotes(0)),
          candidateWithPosition("F0") -> VoteCount(NumPapers(85600), NumVotes(85000)),
          candidateWithPosition("F1") -> VoteCount(NumPapers(0), NumVotes(0)),
          candidateWithPosition("G0") -> VoteCount(NumPapers(5557), NumVotes(5419)),
          candidateWithPosition("G1") -> VoteCount(NumPapers(0), NumVotes(0)),
          candidateWithPosition("H0") -> VoteCount(NumPapers(45134), NumVotes(42682)),
          candidateWithPosition("H1") -> VoteCount(NumPapers(0), NumVotes(0)),
          candidateWithPosition("I0") -> VoteCount(NumPapers(3929), NumVotes(3883)),
          candidateWithPosition("I1") -> VoteCount(NumPapers(0), NumVotes(0)),
          candidateWithPosition("J0") -> VoteCount(NumPapers(12046), NumVotes(11857)),
          candidateWithPosition("J1") -> VoteCount(NumPapers(0), NumVotes(0)),
          candidateWithPosition("UG0") -> VoteCount(NumPapers(0), NumVotes(0)),
          candidateWithPosition("UG1") -> VoteCount(NumPapers(0), NumVotes(0)),
        ),
        exhausted = VoteCount(NumPapers(109), NumVotes(109)),
        roundingError = VoteCount(NumPapers(0), NumVotes(32)),
      ),
      distributionSource = DistributionCountStep.Source(
        candidate = candidateWithPosition("E0"),
        candidateDistributionReason = Exclusion,
        sourceCounts = Set(Count(1), Count(5), Count(7), Count(9), Count(11), Count(13), Count(15), Count(17), Count(19), Count(21), Count(23), Count(25), Count(27)),
        transferValue = TransferValue(1d),
      )
    )

    assert(actualCountData.completedCount.countSteps(Count(29)) === expectedDistributionStep)
  }

  it should "have the correct candidate outcomes" in {
    val expectedOutcomes = CandidateStatuses(
      candidateWithPosition("A0") -> Remaining,
      candidateWithPosition("A1") -> Excluded(Ordinal(4 - 1), Count(8)),
      candidateWithPosition("B0") -> Excluded(Ordinal(11 - 1), Count(22)),
      candidateWithPosition("B1") -> Excluded(Ordinal(1 - 1), Count(2)),
      candidateWithPosition("C0") -> Elected(Ordinal.first, Count(1)),
      candidateWithPosition("C1") -> Remaining,
      candidateWithPosition("D0") -> Excluded(Ordinal(13 - 1), Count(26)),
      candidateWithPosition("D1") -> Excluded(Ordinal(2 - 1), Count(4)),
      candidateWithPosition("E0") -> Excluded(Ordinal(14 - 1), Count(28)),
      candidateWithPosition("E1") -> Excluded(Ordinal(6 - 1), Count(12)),
      candidateWithPosition("F0") -> Elected(Ordinal.second, Count(29)),
      candidateWithPosition("F1") -> Excluded(Ordinal(12 - 1), Count(24)),
      candidateWithPosition("G0") -> Remaining,
      candidateWithPosition("G1") -> Excluded(Ordinal(5 - 1), Count(10)),
      candidateWithPosition("H0") -> Remaining,
      candidateWithPosition("H1") -> Excluded(Ordinal(9 - 1), Count(18)),
      candidateWithPosition("I0") -> Remaining,
      candidateWithPosition("I1") -> Excluded(Ordinal(3 - 1), Count(6)),
      candidateWithPosition("J0") -> Remaining,
      candidateWithPosition("J1") -> Excluded(Ordinal(8 - 1), Count(16)),
      candidateWithPosition("UG0") -> Excluded(Ordinal(10 - 1), Count(20)),
      candidateWithPosition("UG1") -> Excluded(Ordinal(7 - 1), Count(14))
    )

    assert(actualCountData.outcomes === expectedOutcomes)
  }
}
