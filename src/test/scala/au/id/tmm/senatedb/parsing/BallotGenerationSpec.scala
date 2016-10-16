package au.id.tmm.senatedb.parsing

import au.id.tmm.senatedb.fixtures._
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.parsing.Ballot
import au.id.tmm.senatedb.rawdata.model.FormalPreferencesRow
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class BallotGenerationSpec extends ImprovedFlatSpec {

  private def pollingPlaceLookup = PollingPlaces.ACT.pollingPlaceLookup
  private def divisionLookup = Divisions.ACT.divisionLookup

  private val rawPreferenceParser = RawPreferenceParser(SenateElection.`2016`,
    State.ACT, GroupsAndCandidates.ACT.groupsAndCandidates)

  private val ballotMaker = BallotMaker(Candidates.ACT)

  behaviour of "the ballot generator"

  it should "generate a ballot" in {
    val testCsvRow = FormalPreferencesRow("Canberra", "Barton", 1, 1, 1, "4,,3,,,1,5,2,6,,,,,,,,,,,,,,,,,,,,,,,")

    val actualBallot = BallotGeneration.fromFormalPreferencesRows(
      SenateElection.`2016`,
      State.ACT,
      rawPreferenceParser,
      divisionLookup,
      pollingPlaceLookup,
      testCsvRow)

    val expectedBallot = Ballot(SenateElection.`2016`,
      State.ACT,
      Divisions.ACT.CANBERRA,
      PollingPlaces.ACT.BARTON,
      1,
      1,
      ballotMaker.orderedAtlPreferences("F", "H", "C", "A", "G", "I"),
      Map.empty)

    assert(expectedBallot === actualBallot)
  }
}
