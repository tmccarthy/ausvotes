package au.id.tmm.ausvotes.core.parsing

import au.id.tmm.ausvotes.core.fixtures.{BallotMaker, CandidateFixture, GroupAndCandidateFixture}
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class RawPreferenceParserSpec extends ImprovedFlatSpec {

  private val sut = RawPreferenceParser(SenateElection.`2016`, State.ACT, GroupAndCandidateFixture.ACT.groupsAndCandidates)
  private val ballotMaker = BallotMaker(CandidateFixture.ACT)

  behaviour of "the raw preferences parser"

  it should "parse atl preferences from raw preferences" in {
    val (atlPrefs, _) = sut.preferencesFrom("4,,3,,,1,5,2,6,,,,,,8,9,10,,2,11,1,,3,5,,4,,6,,7,,12")

    val expected = ballotMaker.orderedAtlPreferences("F", "H", "C", "A", "G", "I")

    assert(expected == atlPrefs)
  }

  it should "parse btl preferences from raw preferences" in {
    val (_, btlPrefs) = sut.preferencesFrom("4,,3,,,1,5,2,6,,,,,,8,9,10,12,2,11,1,,3,5,,4,,6,,7,,")

    val expected = ballotMaker.orderedBtlPreferences("F0", "E0", "G0", "H1", "G1", "I1", "J1", "C0",
      "C1", "D0", "E1", "D1")

    assert(expected == btlPrefs)
  }

  it should "parse raw preferences with a tick" in {
    val (atlPrefs, _) = sut.preferencesFrom("4,,3,,,/,5,2,6,,,,,,8,9,10,,2,11,1,,3,5,,4,,6,,7,,12")

    val expected = ballotMaker.atlPreferences("F" -> "/", "H" -> "2", "C" -> "3", "A" -> "4", "G" -> "5", "I" -> "6")

    assert(expected == atlPrefs)

  }

  it should "parse raw preferences with a cross" in {
    val (atlPrefs, _) = sut.preferencesFrom("4,,3,,,*,5,2,6,,,,,,8,9,10,,2,11,1,,3,5,,4,,6,,7,,12")

    val expected = ballotMaker.atlPreferences("F" -> "*", "H" -> "2", "C" -> "3", "A" -> "4", "G" -> "5", "I" -> "6")

    assert(expected == atlPrefs)

  }

  it should "parse raw preferences that preference ungrouped candidates" in {
    val (_, btlPrefs) = sut.preferencesFrom("4,,3,,,1,5,2,6,,,,,,8,9,10,,2,11,1,,3,5,,4,,6,,7,,12")

    val expected = ballotMaker.orderedBtlPreferences("F0", "E0", "G0", "H1", "G1", "I1", "J1", "C0", "C1",
      "D0", "E1", "UG1")

    assert(expected == btlPrefs)
  }

  it should "parse raw preferences that preference near the atl/btl boundary" in {
    val (atlPrefs, btlPrefs) =
      sut.preferencesFrom("1,2,3,4,5,6,7,8,9,10,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22")

    val expectedAtl = ballotMaker.orderedAtlPreferences("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
    val expectedBtl = ballotMaker.orderedBtlPreferences("A0", "A1", "B0", "B1", "C0", "C1", "D0", "D1", "E0", "E1",
      "F0", "F1", "G0", "G1", "H0", "H1", "I0", "I1", "J0", "J1", "UG0", "UG1")

    assert(expectedAtl == atlPrefs)
    assert(expectedBtl == btlPrefs)
  }

}
