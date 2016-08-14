package au.id.tmm.senatedb.database.entityconstruction

import au.id.tmm.senatedb.database.model.DAL.UNKNOWN
import au.id.tmm.senatedb.database.model.{AtlPreferencesRow, BallotRow, BtlPreferencesRow}
import au.id.tmm.senatedb.model.{SenateElection, State}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.collection.immutable.ListMap

class formalPreferencesCsvLineToEntitiesSpec extends ImprovedFlatSpec {

  private val csvLine = List("Canberra","Calwell","4","10","30",",,1,5,6,,4,3,,2,,,,,1,2,3,4,5,6,,,7,8,9,10,,,11,12,,")
  private val numCandidatesPerGroup = ListMap(
    "A" -> 2,
    "B" -> 2,
    "C" -> 2,
    "D" -> 2,
    "E" -> 2,
    "F" -> 2,
    "G" -> 2,
    "H" -> 2,
    "I" -> 2,
    "J" -> 2,
    "UG" -> 2
  )

  private val expectedBallotId = "PQg5+wF5dbfBCEqDyU7tSqv7gxRmYyt3I+LUPoZMfF0="

  behaviour of formalPreferencesCsvLineToEntities.getClass.getSimpleName

  it should "create a ballot row as expected" in {
    val (actualBallot, _, _) = formalPreferencesCsvLineToEntities(SenateElection.`2016`, State.ACT, numCandidatesPerGroup, csvLine).get

    val expectedBallot = BallotRow(ballotId = expectedBallotId,
      electionId = "20499",
      state = "ACT",
      electorate = "Canberra",
      voteCollectionPointId = 4,
      batchNo = 10,
      paperNo = 30,
      formal = true,
      expiredAtCount = UNKNOWN)

    assert(actualBallot === expectedBallot)
  }

  it should "create the above the line preferences rows as expected" in {

    val (_, actualPreferences, _) = formalPreferencesCsvLineToEntities(SenateElection.`2016`, State.ACT, numCandidatesPerGroup, csvLine).get

    val expectedPreferences = Set(
      AtlPreferencesRow(expectedBallotId, "C", 1),
      AtlPreferencesRow(expectedBallotId, "J", 2),
      AtlPreferencesRow(expectedBallotId, "H", 3),
      AtlPreferencesRow(expectedBallotId, "G", 4),
      AtlPreferencesRow(expectedBallotId, "D", 5),
      AtlPreferencesRow(expectedBallotId, "E", 6)
    )

    assert(expectedPreferences === actualPreferences)
  }

  it should "create the below the line preferences rows as expected" in {
    val (_, _, actualPreferences) = formalPreferencesCsvLineToEntities(SenateElection.`2016`, State.ACT, numCandidatesPerGroup, csvLine).get

    val expectedPreferences = Set(
      BtlPreferencesRow(expectedBallotId, "G", 0, 8),
      BtlPreferencesRow(expectedBallotId, "C", 1, 3),
      BtlPreferencesRow(expectedBallotId, "D", 0, 4),
      BtlPreferencesRow(expectedBallotId, "F", 1, 7),
      BtlPreferencesRow(expectedBallotId, "I", 1, 11),
      BtlPreferencesRow(expectedBallotId, "H", 0, 10),
      BtlPreferencesRow(expectedBallotId, "J", 0, 12),
      BtlPreferencesRow(expectedBallotId, "B", 1, 1),
      BtlPreferencesRow(expectedBallotId, "D", 1, 5),
      BtlPreferencesRow(expectedBallotId, "E", 0, 6),
      BtlPreferencesRow(expectedBallotId, "C", 0, 2),
      BtlPreferencesRow(expectedBallotId, "G", 1, 9)
    )

    assert(expectedPreferences === actualPreferences)
  }

}
