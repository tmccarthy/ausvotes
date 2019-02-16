package au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.senate_count_data

import au.id.tmm.ausvotes.core.fixtures.{BallotFixture, MockFetchRawFederalElectionData, GroupAndCandidateFixture}
import au.id.tmm.ausvotes.data_sources.aec.federal.raw.FetchRawSenateDistributionOfPreferences
import au.id.tmm.ausvotes.model.federal.senate.{SenateCandidate, SenateCountData, SenateElection, SenateElectionForState}
import au.id.tmm.ausvotes.shared.io.test.BasicTestData
import au.id.tmm.ausvotes.shared.io.test.BasicTestData.BasicTestIO
import au.id.tmm.countstv.model.CandidateStatus._
import au.id.tmm.countstv.model.CandidateStatuses
import au.id.tmm.countstv.model.values.{Count, Ordinal}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.{ImprovedFlatSpec, NeedsCleanDirectory}

class FetchSenateCountDataFromRawWithIneligiblesSpec extends ImprovedFlatSpec with NeedsCleanDirectory {

  private val ballotMaker = BallotFixture.WA.ballotMaker
  import ballotMaker.candidateWithPosition

  private val groupsAndCandidates = GroupAndCandidateFixture.WA.groupsAndCandidates

  private implicit val fetchRawDopRows: FetchRawSenateDistributionOfPreferences[BasicTestIO] = MockFetchRawFederalElectionData

  private implicit val fetcherUnderTest: FetchSenateCountDataFromRaw[BasicTestIO] = FetchSenateCountDataFromRaw[BasicTestIO]

  private def actualCountData: SenateCountData = fetcherUnderTest.senateCountDataFor(SenateElectionForState(SenateElection.`2016`, State.WA).right.get, groupsAndCandidates)
    .run(BasicTestData())
    .result match {
    case Right(countData) => countData
    case Left(exception) => throw exception
  }

  it should "have the correct candidate outcomes" in {
    val expectedOutcomes = CandidateStatuses[SenateCandidate](
      candidateWithPosition("B1") -> Excluded(Ordinal(30 - 1), Count(198)),
      candidateWithPosition("UG1") -> Excluded(Ordinal(20 - 1), Count(124)),
      candidateWithPosition("H1") -> Excluded(Ordinal(17 - 1), Count(101)),
      candidateWithPosition("O0") -> Excluded(Ordinal(54 - 1), Count(413)),
      candidateWithPosition("A1") -> Excluded(Ordinal(23 - 1), Count(140)),
      candidateWithPosition("J1") -> Elected(Ordinal(12 - 1), Count(535)),
      candidateWithPosition("G1") -> Excluded(Ordinal(16 - 1), Count(95)),
      candidateWithPosition("D0") -> Elected(Ordinal(2 - 1), Count(1)),
      candidateWithPosition("D6") -> Excluded(Ordinal(38 - 1), Count(266)),
      candidateWithPosition("L0") -> Excluded(Ordinal(48 - 1), Count(355)),
      candidateWithPosition("S1") -> Excluded(Ordinal(35 - 1), Count(240)),
      candidateWithPosition("H0") -> Excluded(Ordinal(56 - 1), Count(433)),
      candidateWithPosition("E0") -> Excluded(Ordinal(46 - 1), Count(337)),
      candidateWithPosition("U1") -> Excluded(Ordinal(10 - 1), Count(52)),
      candidateWithPosition("R1") -> Elected(Ordinal(11 - 1), Count(535)),
      candidateWithPosition("UG4") -> Excluded(Ordinal(14 - 1), Count(84)),
      candidateWithPosition("J4") -> Excluded(Ordinal(25 - 1), Count(154)),
      candidateWithPosition("R0") -> Ineligible,
      candidateWithPosition("L1") -> Excluded(Ordinal(8 - 1), Count(41)),
      candidateWithPosition("B0") -> Excluded(Ordinal(65 - 1), Count(523)),
      candidateWithPosition("K1") -> Excluded(Ordinal(12 - 1), Count(69)),
      candidateWithPosition("V1") -> Excluded(Ordinal(22 - 1), Count(136)),
      candidateWithPosition("U0") -> Excluded(Ordinal(52 - 1), Count(393)),
      candidateWithPosition("R2") -> Excluded(Ordinal(26 - 1), Count(163)),
      candidateWithPosition("X4") -> Elected(Ordinal(9 - 1), Count(374)),
      candidateWithPosition("C0") -> Excluded(Ordinal(64 - 1), Count(513)),
      candidateWithPosition("E1") -> Excluded(Ordinal(5 - 1), Count(23)),
      candidateWithPosition("A0") -> Excluded(Ordinal(59 - 1), Count(463)),
      candidateWithPosition("Q0") -> Excluded(Ordinal(51 - 1), Count(383)),
      candidateWithPosition("F2") -> Excluded(Ordinal(33 - 1), Count(223)),
      candidateWithPosition("D2") -> Elected(Ordinal(7 - 1), Count(6)),
      candidateWithPosition("I0") -> Excluded(Ordinal(53 - 1), Count(403)),
      candidateWithPosition("AA0") -> Excluded(Ordinal(42 - 1), Count(302)),
      candidateWithPosition("X6") -> Excluded(Ordinal(37 - 1), Count(258)),
      candidateWithPosition("T1") -> Excluded(Ordinal(15 - 1), Count(86)),
      candidateWithPosition("X0") -> Elected(Ordinal(1 - 1), Count(1)),
      candidateWithPosition("S0") -> Excluded(Ordinal(62 - 1), Count(493)),
      candidateWithPosition("M1") -> Excluded(Ordinal(2 - 1), Count(11)),
      candidateWithPosition("Y0") -> Excluded(Ordinal(49 - 1), Count(364)),
      candidateWithPosition("J3") -> Excluded(Ordinal(39 - 1), Count(275)),
      candidateWithPosition("X3") -> Elected(Ordinal(8 - 1), Count(7)),
      candidateWithPosition("AA1") -> Excluded(Ordinal(1 - 1), Count(9)),
      candidateWithPosition("W0") -> Excluded(Ordinal(63 - 1), Count(503)),
      candidateWithPosition("UG3") -> Excluded(Ordinal(29 - 1), Count(190)),
      candidateWithPosition("D1") -> Elected(Ordinal(5 - 1), Count(3)),
      candidateWithPosition("T0") -> Excluded(Ordinal(55 - 1), Count(423)),
      candidateWithPosition("AB0") -> Excluded(Ordinal(57 - 1), Count(443)),
      candidateWithPosition("N0") -> Excluded(Ordinal(47 - 1), Count(346)),
      candidateWithPosition("P0") -> Excluded(Ordinal(60 - 1), Count(473)),
      candidateWithPosition("J2") -> Excluded(Ordinal(27 - 1), Count(172)),
      candidateWithPosition("D5") -> Excluded(Ordinal(34 - 1), Count(231)),
      candidateWithPosition("UG0") -> Excluded(Ordinal(32 - 1), Count(216)),
      candidateWithPosition("X5") -> Excluded(Ordinal(43 - 1), Count(311)),
      candidateWithPosition("O1") -> Excluded(Ordinal(18 - 1), Count(108)),
      candidateWithPosition("P1") -> Excluded(Ordinal(9 - 1), Count(48)),
      candidateWithPosition("X1") -> Elected(Ordinal(4 - 1), Count(2)),
      candidateWithPosition("Z1") -> Excluded(Ordinal(19 - 1), Count(116)),
      candidateWithPosition("C1") -> Excluded(Ordinal(28 - 1), Count(181)),
      candidateWithPosition("J0") -> Elected(Ordinal(3 - 1), Count(1)),
      candidateWithPosition("X2") -> Elected(Ordinal(6 - 1), Count(5)),
      candidateWithPosition("Q1") -> Excluded(Ordinal(4 - 1), Count(20)),
      candidateWithPosition("D3") -> Elected(Ordinal(10 - 1), Count(524)),
      candidateWithPosition("M0") -> Excluded(Ordinal(50 - 1), Count(373)),
      candidateWithPosition("V0") -> Excluded(Ordinal(40 - 1), Count(284)),
      candidateWithPosition("W1") -> Excluded(Ordinal(24 - 1), Count(147)),
      candidateWithPosition("J5") -> Excluded(Ordinal(31 - 1), Count(207)),
      candidateWithPosition("Y1") -> Excluded(Ordinal(3 - 1), Count(15)),
      candidateWithPosition("Z0") -> Excluded(Ordinal(58 - 1), Count(453)),
      candidateWithPosition("F1") -> Excluded(Ordinal(36 - 1), Count(249)),
      candidateWithPosition("UG2") -> Excluded(Ordinal(44 - 1), Count(320)),
      candidateWithPosition("UG5") -> Excluded(Ordinal(21 - 1), Count(132)),
      candidateWithPosition("I1") -> Excluded(Ordinal(6 - 1), Count(27)),
      candidateWithPosition("F0") -> Excluded(Ordinal(66 - 1), Count(534)),
      candidateWithPosition("D4") -> Excluded(Ordinal(41 - 1), Count(293)),
      candidateWithPosition("AB1") -> Excluded(Ordinal(11 - 1), Count(61)),
      candidateWithPosition("G0") -> Excluded(Ordinal(45 - 1), Count(329)),
      candidateWithPosition("N1") -> Excluded(Ordinal(13 - 1), Count(76)),
      candidateWithPosition("K0") -> Excluded(Ordinal(61 - 1), Count(483)),
      candidateWithPosition("G2") -> Excluded(Ordinal(7 - 1), Count(35)),
    )

    assert(actualCountData.completedCount.outcomes === expectedOutcomes)
  }

}
