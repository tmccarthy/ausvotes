package au.id.tmm.ausvotes.core.parsing

import au.id.tmm.ausvotes.core.fixtures._
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.{Ballot, VoteCollectionPoint}
import au.id.tmm.ausvotes.core.rawdata.model.FormalPreferencesRow
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class BallotGenerationSpec extends ImprovedFlatSpec {

  private def pollingPlaceLookup = PollingPlaceFixture.ACT.pollingPlaceLookup
  private def divisionLookup = DivisionFixture.ACT.divisionLookup

  private val rawPreferenceParser = RawPreferenceParser(SenateElection.`2016`,
    State.ACT, GroupAndCandidateFixture.ACT.groupsAndCandidates)

  private val ballotMaker = BallotMaker(CandidateFixture.ACT)

  "the ballot generator" should "generate a ballot" in {
    val testCsvRow = FormalPreferencesRow("Canberra", "Barton", 1, 1, 1, "4,,3,,,1,5,2,6,,,,,,,,,,,,,,,,,,,,,,,")

    val actualBallot = generateBallotFrom(testCsvRow)

    val expectedBallot = Ballot(SenateElection.`2016`,
      State.ACT,
      DivisionFixture.ACT.CANBERRA,
      PollingPlaceFixture.ACT.BARTON,
      1,
      1,
      ballotMaker.orderedAtlPreferences("F", "H", "C", "A", "G", "I"),
      Map.empty)

    assert(expectedBallot === actualBallot)
  }

  it should "generate a ballot from a postal vote collection point" in {
    val testCsvRow = FormalPreferencesRow("Canberra", "POSTAL 2", 1, 1, 1, "4,,3,,,1,5,2,6,,,,,,,,,,,,,,,,,,,,,,,")

    val actualBallot: Ballot = generateBallotFrom(testCsvRow)

    val expectedBallot = Ballot(SenateElection.`2016`,
      State.ACT,
      DivisionFixture.ACT.CANBERRA,
      VoteCollectionPoint.Postal(SenateElection.`2016`, State.ACT, DivisionFixture.ACT.CANBERRA, 2),
      1,
      1,
      ballotMaker.orderedAtlPreferences("F", "H", "C", "A", "G", "I"),
      Map.empty)

    assert(expectedBallot === actualBallot)
  }

  it should "generate a ballot from an absentee vote collection point" in {
    val testCsvRow = FormalPreferencesRow("Canberra", "ABSENT 2", 1, 1, 1, "4,,3,,,1,5,2,6,,,,,,,,,,,,,,,,,,,,,,,")

    val actualBallot: Ballot = generateBallotFrom(testCsvRow)

    val expectedBallot = Ballot(SenateElection.`2016`,
      State.ACT,
      DivisionFixture.ACT.CANBERRA,
      VoteCollectionPoint.Absentee(SenateElection.`2016`, State.ACT, DivisionFixture.ACT.CANBERRA, 2),
      1,
      1,
      ballotMaker.orderedAtlPreferences("F", "H", "C", "A", "G", "I"),
      Map.empty)

    assert(expectedBallot === actualBallot)
  }

  it should "generate a ballot from an pre-poll vote collection point" in {
    val testCsvRow = FormalPreferencesRow("Canberra", "PRE_POLL 2", 1, 1, 1, "4,,3,,,1,5,2,6,,,,,,,,,,,,,,,,,,,,,,,")

    val actualBallot: Ballot = generateBallotFrom(testCsvRow)

    val expectedBallot = Ballot(SenateElection.`2016`,
      State.ACT,
      DivisionFixture.ACT.CANBERRA,
      VoteCollectionPoint.PrePoll(SenateElection.`2016`, State.ACT, DivisionFixture.ACT.CANBERRA, 2),
      1,
      1,
      ballotMaker.orderedAtlPreferences("F", "H", "C", "A", "G", "I"),
      Map.empty)

    assert(expectedBallot === actualBallot)
  }

  it should "generate a ballot from an provisional vote collection point" in {
    val testCsvRow = FormalPreferencesRow("Canberra", "PROVISIONAL 2", 1, 1, 1, "4,,3,,,1,5,2,6,,,,,,,,,,,,,,,,,,,,,,,")

    val actualBallot: Ballot = generateBallotFrom(testCsvRow)

    val expectedBallot = Ballot(SenateElection.`2016`,
      State.ACT,
      DivisionFixture.ACT.CANBERRA,
      VoteCollectionPoint.Provisional(SenateElection.`2016`, State.ACT, DivisionFixture.ACT.CANBERRA, 2),
      1,
      1,
      ballotMaker.orderedAtlPreferences("F", "H", "C", "A", "G", "I"),
      Map.empty)

    assert(expectedBallot === actualBallot)
  }

  def generateBallotFrom(testCsvRow: FormalPreferencesRow): Ballot = {
    val actualBallot = BallotGeneration.fromFormalPreferencesRow(
      SenateElection.`2016`,
      State.ACT,
      rawPreferenceParser,
      divisionLookup,
      (state, name) => pollingPlaceLookup(name),
      testCsvRow)

    actualBallot
  }
}
