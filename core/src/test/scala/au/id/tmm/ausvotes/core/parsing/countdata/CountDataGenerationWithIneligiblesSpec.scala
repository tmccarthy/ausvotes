package au.id.tmm.ausvotes.core.parsing.countdata

import au.id.tmm.ausvotes.core.fixtures._
import au.id.tmm.ausvotes.core.model.parsing.CandidatePosition
import au.id.tmm.countstv.model.CandidateStatus._
import au.id.tmm.countstv.model.CandidateStatuses
import au.id.tmm.countstv.model.values.{Count, Ordinal}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountDataGenerationWithIneligiblesSpec extends ImprovedFlatSpec {

  import CountDataTestUtils.WA._

  private lazy val actualCountData = countData

  it should "have the correct candidate outcomes" in {
    val expectedOutcomes = CandidateStatuses[CandidatePosition](
      candidatePosition("B1") -> Excluded(Ordinal(30 - 1), Count(198)),
      candidatePosition("UG1") -> Excluded(Ordinal(20 - 1), Count(124)),
      candidatePosition("H1") -> Excluded(Ordinal(17 - 1), Count(101)),
      candidatePosition("O0") -> Excluded(Ordinal(54 - 1), Count(413)),
      candidatePosition("A1") -> Excluded(Ordinal(23 - 1), Count(140)),
      candidatePosition("J1") -> Elected(Ordinal(12 - 1), Count(535)),
      candidatePosition("G1") -> Excluded(Ordinal(16 - 1), Count(95)),
      candidatePosition("D0") -> Elected(Ordinal(2 - 1), Count(1)),
      candidatePosition("D6") -> Excluded(Ordinal(38 - 1), Count(266)),
      candidatePosition("L0") -> Excluded(Ordinal(48 - 1), Count(355)),
      candidatePosition("S1") -> Excluded(Ordinal(35 - 1), Count(240)),
      candidatePosition("H0") -> Excluded(Ordinal(56 - 1), Count(433)),
      candidatePosition("E0") -> Excluded(Ordinal(46 - 1), Count(337)),
      candidatePosition("U1") -> Excluded(Ordinal(10 - 1), Count(52)),
      candidatePosition("R1") -> Elected(Ordinal(11 - 1), Count(535)),
      candidatePosition("UG4") -> Excluded(Ordinal(14 - 1), Count(84)),
      candidatePosition("J4") -> Excluded(Ordinal(25 - 1), Count(154)),
      candidatePosition("R0") -> Ineligible,
      candidatePosition("L1") -> Excluded(Ordinal(8 - 1), Count(41)),
      candidatePosition("B0") -> Excluded(Ordinal(65 - 1), Count(523)),
      candidatePosition("K1") -> Excluded(Ordinal(12 - 1), Count(69)),
      candidatePosition("V1") -> Excluded(Ordinal(22 - 1), Count(136)),
      candidatePosition("U0") -> Excluded(Ordinal(52 - 1), Count(393)),
      candidatePosition("R2") -> Excluded(Ordinal(26 - 1), Count(163)),
      candidatePosition("X4") -> Elected(Ordinal(9 - 1), Count(374)),
      candidatePosition("C0") -> Excluded(Ordinal(64 - 1), Count(513)),
      candidatePosition("E1") -> Excluded(Ordinal(5 - 1), Count(23)),
      candidatePosition("A0") -> Excluded(Ordinal(59 - 1), Count(463)),
      candidatePosition("Q0") -> Excluded(Ordinal(51 - 1), Count(383)),
      candidatePosition("F2") -> Excluded(Ordinal(33 - 1), Count(223)),
      candidatePosition("D2") -> Elected(Ordinal(7 - 1), Count(6)),
      candidatePosition("I0") -> Excluded(Ordinal(53 - 1), Count(403)),
      candidatePosition("AA0") -> Excluded(Ordinal(42 - 1), Count(302)),
      candidatePosition("X6") -> Excluded(Ordinal(37 - 1), Count(258)),
      candidatePosition("T1") -> Excluded(Ordinal(15 - 1), Count(86)),
      candidatePosition("X0") -> Elected(Ordinal(1 - 1), Count(1)),
      candidatePosition("S0") -> Excluded(Ordinal(62 - 1), Count(493)),
      candidatePosition("M1") -> Excluded(Ordinal(2 - 1), Count(11)),
      candidatePosition("Y0") -> Excluded(Ordinal(49 - 1), Count(364)),
      candidatePosition("J3") -> Excluded(Ordinal(39 - 1), Count(275)),
      candidatePosition("X3") -> Elected(Ordinal(8 - 1), Count(7)),
      candidatePosition("AA1") -> Excluded(Ordinal(1 - 1), Count(9)),
      candidatePosition("W0") -> Excluded(Ordinal(63 - 1), Count(503)),
      candidatePosition("UG3") -> Excluded(Ordinal(29 - 1), Count(190)),
      candidatePosition("D1") -> Elected(Ordinal(5 - 1), Count(3)),
      candidatePosition("T0") -> Excluded(Ordinal(55 - 1), Count(423)),
      candidatePosition("AB0") -> Excluded(Ordinal(57 - 1), Count(443)),
      candidatePosition("N0") -> Excluded(Ordinal(47 - 1), Count(346)),
      candidatePosition("P0") -> Excluded(Ordinal(60 - 1), Count(473)),
      candidatePosition("J2") -> Excluded(Ordinal(27 - 1), Count(172)),
      candidatePosition("D5") -> Excluded(Ordinal(34 - 1), Count(231)),
      candidatePosition("UG0") -> Excluded(Ordinal(32 - 1), Count(216)),
      candidatePosition("X5") -> Excluded(Ordinal(43 - 1), Count(311)),
      candidatePosition("O1") -> Excluded(Ordinal(18 - 1), Count(108)),
      candidatePosition("P1") -> Excluded(Ordinal(9 - 1), Count(48)),
      candidatePosition("X1") -> Elected(Ordinal(4 - 1), Count(2)),
      candidatePosition("Z1") -> Excluded(Ordinal(19 - 1), Count(116)),
      candidatePosition("C1") -> Excluded(Ordinal(28 - 1), Count(181)),
      candidatePosition("J0") -> Elected(Ordinal(3 - 1), Count(1)),
      candidatePosition("X2") -> Elected(Ordinal(6 - 1), Count(5)),
      candidatePosition("Q1") -> Excluded(Ordinal(4 - 1), Count(20)),
      candidatePosition("D3") -> Elected(Ordinal(10 - 1), Count(524)),
      candidatePosition("M0") -> Excluded(Ordinal(50 - 1), Count(373)),
      candidatePosition("V0") -> Excluded(Ordinal(40 - 1), Count(284)),
      candidatePosition("W1") -> Excluded(Ordinal(24 - 1), Count(147)),
      candidatePosition("J5") -> Excluded(Ordinal(31 - 1), Count(207)),
      candidatePosition("Y1") -> Excluded(Ordinal(3 - 1), Count(15)),
      candidatePosition("Z0") -> Excluded(Ordinal(58 - 1), Count(453)),
      candidatePosition("F1") -> Excluded(Ordinal(36 - 1), Count(249)),
      candidatePosition("UG2") -> Excluded(Ordinal(44 - 1), Count(320)),
      candidatePosition("UG5") -> Excluded(Ordinal(21 - 1), Count(132)),
      candidatePosition("I1") -> Excluded(Ordinal(6 - 1), Count(27)),
      candidatePosition("F0") -> Excluded(Ordinal(66 - 1), Count(534)),
      candidatePosition("D4") -> Excluded(Ordinal(41 - 1), Count(293)),
      candidatePosition("AB1") -> Excluded(Ordinal(11 - 1), Count(61)),
      candidatePosition("G0") -> Excluded(Ordinal(45 - 1), Count(329)),
      candidatePosition("N1") -> Excluded(Ordinal(13 - 1), Count(76)),
      candidatePosition("K0") -> Excluded(Ordinal(61 - 1), Count(483)),
      candidatePosition("G2") -> Excluded(Ordinal(7 - 1), Count(35)),
    )

    assert(actualCountData.outcomes === expectedOutcomes)
  }

  def candidatePosition(positionCode: String): CandidatePosition =
    BallotMaker.candidatePosition(GroupFixture.WA)(positionCode)
}
