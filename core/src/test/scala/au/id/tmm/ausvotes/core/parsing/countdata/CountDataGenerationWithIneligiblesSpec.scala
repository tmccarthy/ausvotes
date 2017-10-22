package au.id.tmm.ausvotes.core.parsing.countdata

import au.id.tmm.ausvotes.core.fixtures._
import au.id.tmm.ausvotes.core.model.CountData.CountOutcome
import au.id.tmm.ausvotes.core.model.parsing.CandidatePosition
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CountDataGenerationWithIneligiblesSpec extends ImprovedFlatSpec {

  import CountDataTestUtils.WA._

  private lazy val actualCountData = countData

  it should "have the correct candidate outcomes" in {
    val expectedOutcomes: Map[CandidatePosition, CountOutcome] = Map(
      candidatePosition("B1") -> CountOutcome.Excluded(30,198),
      candidatePosition("UG1") -> CountOutcome.Excluded(20,124),
      candidatePosition("H1") -> CountOutcome.Excluded(17,101),
      candidatePosition("O0") -> CountOutcome.Excluded(54,413),
      candidatePosition("A1") -> CountOutcome.Excluded(23,140),
      candidatePosition("J1") -> CountOutcome.Elected(12,535),
      candidatePosition("G1") -> CountOutcome.Excluded(16,95),
      candidatePosition("D0") -> CountOutcome.Elected(2,1),
      candidatePosition("D6") -> CountOutcome.Excluded(38,266),
      candidatePosition("L0") -> CountOutcome.Excluded(48,355),
      candidatePosition("S1") -> CountOutcome.Excluded(35,240),
      candidatePosition("H0") -> CountOutcome.Excluded(56,433),
      candidatePosition("E0") -> CountOutcome.Excluded(46,337),
      candidatePosition("U1") -> CountOutcome.Excluded(10,52),
      candidatePosition("R1") -> CountOutcome.Elected(11,535),
      candidatePosition("UG4") -> CountOutcome.Excluded(14,84),
      candidatePosition("J4") -> CountOutcome.Excluded(25,154),
      candidatePosition("R0") -> CountOutcome.Ineligible,
      candidatePosition("L1") -> CountOutcome.Excluded(8,41),
      candidatePosition("B0") -> CountOutcome.Excluded(65,523),
      candidatePosition("K1") -> CountOutcome.Excluded(12,69),
      candidatePosition("V1") -> CountOutcome.Excluded(22,136),
      candidatePosition("U0") -> CountOutcome.Excluded(52,393),
      candidatePosition("R2") -> CountOutcome.Excluded(26,163),
      candidatePosition("X4") -> CountOutcome.Elected(9,374),
      candidatePosition("C0") -> CountOutcome.Excluded(64,513),
      candidatePosition("E1") -> CountOutcome.Excluded(5,23),
      candidatePosition("A0") -> CountOutcome.Excluded(59,463),
      candidatePosition("Q0") -> CountOutcome.Excluded(51,383),
      candidatePosition("F2") -> CountOutcome.Excluded(33,223),
      candidatePosition("D2") -> CountOutcome.Elected(7,6),
      candidatePosition("I0") -> CountOutcome.Excluded(53,403),
      candidatePosition("AA0") -> CountOutcome.Excluded(42,302),
      candidatePosition("X6") -> CountOutcome.Excluded(37,258),
      candidatePosition("T1") -> CountOutcome.Excluded(15,86),
      candidatePosition("X0") -> CountOutcome.Elected(1,1),
      candidatePosition("S0") -> CountOutcome.Excluded(62,493),
      candidatePosition("M1") -> CountOutcome.Excluded(2,11),
      candidatePosition("Y0") -> CountOutcome.Excluded(49,364),
      candidatePosition("J3") -> CountOutcome.Excluded(39,275),
      candidatePosition("X3") -> CountOutcome.Elected(8,7),
      candidatePosition("AA1") -> CountOutcome.Excluded(1,9),
      candidatePosition("W0") -> CountOutcome.Excluded(63,503),
      candidatePosition("UG3") -> CountOutcome.Excluded(29,190),
      candidatePosition("D1") -> CountOutcome.Elected(5,3),
      candidatePosition("T0") -> CountOutcome.Excluded(55,423),
      candidatePosition("AB0") -> CountOutcome.Excluded(57,443),
      candidatePosition("N0") -> CountOutcome.Excluded(47,346),
      candidatePosition("P0") -> CountOutcome.Excluded(60,473),
      candidatePosition("J2") -> CountOutcome.Excluded(27,172),
      candidatePosition("D5") -> CountOutcome.Excluded(34,231),
      candidatePosition("UG0") -> CountOutcome.Excluded(32,216),
      candidatePosition("X5") -> CountOutcome.Excluded(43,311),
      candidatePosition("O1") -> CountOutcome.Excluded(18,108),
      candidatePosition("P1") -> CountOutcome.Excluded(9,48),
      candidatePosition("X1") -> CountOutcome.Elected(4,2),
      candidatePosition("Z1") -> CountOutcome.Excluded(19,116),
      candidatePosition("C1") -> CountOutcome.Excluded(28,181),
      candidatePosition("J0") -> CountOutcome.Elected(3,1),
      candidatePosition("X2") -> CountOutcome.Elected(6,5),
      candidatePosition("Q1") -> CountOutcome.Excluded(4,20),
      candidatePosition("D3") -> CountOutcome.Elected(10,524),
      candidatePosition("M0") -> CountOutcome.Excluded(50,373),
      candidatePosition("V0") -> CountOutcome.Excluded(40,284),
      candidatePosition("W1") -> CountOutcome.Excluded(24,147),
      candidatePosition("J5") -> CountOutcome.Excluded(31,207),
      candidatePosition("Y1") -> CountOutcome.Excluded(3,15),
      candidatePosition("Z0") -> CountOutcome.Excluded(58,453),
      candidatePosition("F1") -> CountOutcome.Excluded(36,249),
      candidatePosition("UG2") -> CountOutcome.Excluded(44,320),
      candidatePosition("UG5") -> CountOutcome.Excluded(21,132),
      candidatePosition("I1") -> CountOutcome.Excluded(6,27),
      candidatePosition("F0") -> CountOutcome.Excluded(66,534),
      candidatePosition("D4") -> CountOutcome.Excluded(41,293),
      candidatePosition("AB1") -> CountOutcome.Excluded(11,61),
      candidatePosition("G0") -> CountOutcome.Excluded(45,329),
      candidatePosition("N1") -> CountOutcome.Excluded(13,76),
      candidatePosition("K0") -> CountOutcome.Excluded(61,483),
      candidatePosition("G2") -> CountOutcome.Excluded(7,35),
    )

    assert(actualCountData.outcomes === expectedOutcomes)
  }

  def candidatePosition(positionCode: String): CandidatePosition =
    BallotMaker.candidatePosition(GroupFixture.WA)(positionCode)
}
