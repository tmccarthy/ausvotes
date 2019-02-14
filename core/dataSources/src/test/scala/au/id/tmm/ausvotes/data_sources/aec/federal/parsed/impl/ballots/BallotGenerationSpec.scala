package au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.ballots

import au.id.tmm.ausvotes.core.fixtures._
import au.id.tmm.ausvotes.data_sources.aec.federal.raw.FetchRawFormalSenatePreferences
import au.id.tmm.ausvotes.model.VoteCollectionPoint
import au.id.tmm.ausvotes.model.VoteCollectionPoint.Special.SpecialVcpType
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.ausvotes.model.federal.{FederalBallotJurisdiction, FederalElection, FederalVcpJurisdiction}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class BallotGenerationSpec extends ImprovedFlatSpec {

  private def pollingPlaceLookup = PollingPlaceFixture.ACT.pollingPlaceLookup
  private def divisionLookup = DivisionFixture.ACT.divisionLookup

  private val rawPreferenceParser =
    RawPreferenceParser(GroupAndCandidateFixture.ACT.election, GroupAndCandidateFixture.ACT.groupsAndCandidates)

  private val ballotMaker = BallotMaker(CandidateFixture.ACT)

  "the ballot generator" should "generate a ballot" in {
    val testCsvRow = FetchRawFormalSenatePreferences.Row("Canberra", "Barton", 1, 1, 1, "4,,3,,,1,5,2,6,,,,,,,,,,,,,,,,,,,,,,,")

    val actualBallot = generateBallotFrom(testCsvRow)

    val expectedBallot = SenateBallot(
      GroupAndCandidateFixture.ACT.election,
      FederalBallotJurisdiction(
        State.ACT,
        DivisionFixture.ACT.CANBERRA,
        PollingPlaceFixture.ACT.BARTON,
      ),
      SenateBallotId(
        batch = 1,
        paper = 1,
      ),
      groupPreferences = ballotMaker.orderedAtlPreferences("F", "H", "C", "A", "G", "I"),
      candidatePreferences = Map.empty,
    )

    assert(expectedBallot === actualBallot)
  }

  it should "generate a ballot from a postal vote collection point" in {
    val testCsvRow = FetchRawFormalSenatePreferences.Row("Canberra", "POSTAL 2", 1, 1, 1, "4,,3,,,1,5,2,6,,,,,,,,,,,,,,,,,,,,,,,")

    val actualBallot: SenateBallot = generateBallotFrom(testCsvRow)

    val expectedBallot = SenateBallot(
      election = CandidateFixture.ACT.election,
      FederalBallotJurisdiction(
        State.ACT,
        DivisionFixture.ACT.CANBERRA,
        VoteCollectionPoint.Special(FederalElection.`2016`, FederalVcpJurisdiction(State.ACT, DivisionFixture.ACT.CANBERRA), SpecialVcpType.Postal, VoteCollectionPoint.Special.Id(2)),
      ),
      SenateBallotId(
        1,
        1,
      ),
      ballotMaker.orderedAtlPreferences("F", "H", "C", "A", "G", "I"),
      Map.empty)

    assert(expectedBallot === actualBallot)
  }

  it should "generate a ballot from an absentee vote collection point" in {
    val testCsvRow = FetchRawFormalSenatePreferences.Row("Canberra", "ABSENT 2", 1, 1, 1, "4,,3,,,1,5,2,6,,,,,,,,,,,,,,,,,,,,,,,")

    val actualBallot: SenateBallot = generateBallotFrom(testCsvRow)

    val expectedBallot = SenateBallot(
      election = CandidateFixture.ACT.election,
      FederalBallotJurisdiction(
        State.ACT,
        DivisionFixture.ACT.CANBERRA,
        VoteCollectionPoint.Special(FederalElection.`2016`, FederalVcpJurisdiction(State.ACT, DivisionFixture.ACT.CANBERRA), SpecialVcpType.Absentee, VoteCollectionPoint.Special.Id(2)),
      ),
      SenateBallotId(
        1,
        1,
      ),
      ballotMaker.orderedAtlPreferences("F", "H", "C", "A", "G", "I"),
      Map.empty)

    assert(expectedBallot === actualBallot)
  }

  it should "generate a ballot from an pre-poll vote collection point" in {
    val testCsvRow = FetchRawFormalSenatePreferences.Row("Canberra", "PRE_POLL 2", 1, 1, 1, "4,,3,,,1,5,2,6,,,,,,,,,,,,,,,,,,,,,,,")

    val actualBallot: SenateBallot = generateBallotFrom(testCsvRow)

    val expectedBallot = SenateBallot(
      election = CandidateFixture.ACT.election,
      FederalBallotJurisdiction(
        State.ACT,
        DivisionFixture.ACT.CANBERRA,
        VoteCollectionPoint.Special(FederalElection.`2016`, FederalVcpJurisdiction(State.ACT, DivisionFixture.ACT.CANBERRA), SpecialVcpType.PrePoll, VoteCollectionPoint.Special.Id(2)),
      ),
      SenateBallotId(
        1,
        1,
      ),
      ballotMaker.orderedAtlPreferences("F", "H", "C", "A", "G", "I"),
      Map.empty)

    assert(expectedBallot === actualBallot)
  }

  it should "generate a ballot from an provisional vote collection point" in {
    val testCsvRow = FetchRawFormalSenatePreferences.Row("Canberra", "PROVISIONAL 2", 1, 1, 1, "4,,3,,,1,5,2,6,,,,,,,,,,,,,,,,,,,,,,,")

    val actualBallot: SenateBallot = generateBallotFrom(testCsvRow)

    val expectedBallot = SenateBallot(
      election = CandidateFixture.ACT.election,
      FederalBallotJurisdiction(
        State.ACT,
        DivisionFixture.ACT.CANBERRA,
        VoteCollectionPoint.Special(FederalElection.`2016`, FederalVcpJurisdiction(State.ACT, DivisionFixture.ACT.CANBERRA), SpecialVcpType.Provisional, VoteCollectionPoint.Special.Id(2)),
      ),
      SenateBallotId(
        1,
        1,
      ),
      ballotMaker.orderedAtlPreferences("F", "H", "C", "A", "G", "I"),
      Map.empty)

    assert(expectedBallot === actualBallot)
  }

  def generateBallotFrom(testCsvRow: FetchRawFormalSenatePreferences.Row): SenateBallot = {
    val actualBallot = BallotGeneration.fromFormalPreferencesRow(
      CandidateFixture.ACT.election,
      rawPreferenceParser,
      divisionLookup,
      (state, name) => pollingPlaceLookup(name),
      testCsvRow)

    actualBallot
  }
}
