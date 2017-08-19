package au.id.tmm.senatedb.core.model.parsing

import au.id.tmm.senatedb.core.fixtures.{BallotMaker, CandidateFixture, GroupAndCandidateFixture}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CandidatePositionSpec extends ImprovedFlatSpec {

  private val groupsAndCandidates = GroupAndCandidateFixture.ACT.groupsAndCandidates
  private val ballotMaker = BallotMaker(CandidateFixture.ACT)

  "the ballot position to candidate position map" should "be constructed correctly" in {
    import ballotMaker.candidatePosition

    val actual = CandidatePosition.constructBallotPositionLookup(groupsAndCandidates)
    val expected = Map(
      11 -> candidatePosition("A0"),
      12 -> candidatePosition("A1"),
      13 -> candidatePosition("B0"),
      14 -> candidatePosition("B1"),
      15 -> candidatePosition("C0"),
      16 -> candidatePosition("C1"),
      17 -> candidatePosition("D0"),
      18 -> candidatePosition("D1"),
      19 -> candidatePosition("E0"),
      20 -> candidatePosition("E1"),
      21 -> candidatePosition("F0"),
      22 -> candidatePosition("F1"),
      23 -> candidatePosition("G0"),
      24 -> candidatePosition("G1"),
      25 -> candidatePosition("H0"),
      26 -> candidatePosition("H1"),
      27 -> candidatePosition("I0"),
      28 -> candidatePosition("I1"),
      29 -> candidatePosition("J0"),
      30 -> candidatePosition("J1"),
      31 -> candidatePosition("UG0"),
      32 -> candidatePosition("UG1")
    )

    assert(expected === actual)
  }

}
