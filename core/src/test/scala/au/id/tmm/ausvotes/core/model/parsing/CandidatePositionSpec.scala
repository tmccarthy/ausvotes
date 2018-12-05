package au.id.tmm.ausvotes.core.model.parsing

import au.id.tmm.ausvotes.core.fixtures.{BallotMaker, CandidateFixture, GroupAndCandidateFixture}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CandidatePositionSpec extends ImprovedFlatSpec {

  private val groupsAndCandidates = GroupAndCandidateFixture.ACT.groupsAndCandidates
  private val ballotMaker = BallotMaker(CandidateFixture.ACT)

  import ballotMaker.{candidatePosition, candidateWithPosition}

  "a candidate position" should "have a code" in {
    assert(candidatePosition("A0").code === "A0")
  }

  "the ballot position to candidate map" should "be constructed correctly" in {
    val actual = CandidatePosition.constructBallotPositionLookup(groupsAndCandidates)
    val expected = Map(
      11 -> candidateWithPosition("A0"),
      12 -> candidateWithPosition("A1"),
      13 -> candidateWithPosition("B0"),
      14 -> candidateWithPosition("B1"),
      15 -> candidateWithPosition("C0"),
      16 -> candidateWithPosition("C1"),
      17 -> candidateWithPosition("D0"),
      18 -> candidateWithPosition("D1"),
      19 -> candidateWithPosition("E0"),
      20 -> candidateWithPosition("E1"),
      21 -> candidateWithPosition("F0"),
      22 -> candidateWithPosition("F1"),
      23 -> candidateWithPosition("G0"),
      24 -> candidateWithPosition("G1"),
      25 -> candidateWithPosition("H0"),
      26 -> candidateWithPosition("H1"),
      27 -> candidateWithPosition("I0"),
      28 -> candidateWithPosition("I1"),
      29 -> candidateWithPosition("J0"),
      30 -> candidateWithPosition("J1"),
      31 -> candidateWithPosition("UG0"),
      32 -> candidateWithPosition("UG1")
    )

    assert(expected === actual)
  }

}
