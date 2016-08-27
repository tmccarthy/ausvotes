package au.id.tmm.senatedb.data.rawdatastore.entityconstruction

import au.id.tmm.senatedb.data.database.{AtlPreferencesRow, BallotFactsRow, BallotRow, BtlPreferencesRow}
import au.id.tmm.senatedb.model.{SenateElection, State}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.collection.immutable.ListMap

class formalPreferencesCsvLineToEntitiesSpec extends ImprovedFlatSpec {

  private val csvLine = List("Canberra","Calwell","4","10","30",",,1,5,6,,4,3,,2,,*,,,1,2,3,4,5,6,,,7,8,9,10,,/,11,12,,")
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
    val (actualBallot, _, _, _) = formalPreferencesCsvLineToEntities(SenateElection.`2016`, State.ACT, numCandidatesPerGroup, csvLine).get.unapply

    val expectedBallot = BallotRow(ballotId = expectedBallotId,
      electionId = "20499",
      state = "ACT",
      electorate = "Canberra",
      voteCollectionPointId = 4,
      batchNo = 10,
      paperNo = 30
    )

    assert(actualBallot === expectedBallot)
  }

  it should "create the ballot facts as expected" in {
    val (_, actualBallotFacts, _, _) = formalPreferencesCsvLineToEntities(SenateElection.`2016`, State.ACT, numCandidatesPerGroup, csvLine).get.unapply

    val expectedBallotFacts = BallotFactsRow(expectedBallotId, 6, 12, false, true)

    assert(actualBallotFacts === expectedBallotFacts)
  }

  it should "create the above the line preferences rows as expected" in {

    val (_, _, actualPreferences, _) = formalPreferencesCsvLineToEntities(SenateElection.`2016`, State.ACT, numCandidatesPerGroup, csvLine).get.unapply

    val expectedPreferences = Set(
      AtlPreferencesRow(expectedBallotId, "C", Some(1), None),
      AtlPreferencesRow(expectedBallotId, "J", Some(2), None),
      AtlPreferencesRow(expectedBallotId, "H", Some(3), None),
      AtlPreferencesRow(expectedBallotId, "G", Some(4), None),
      AtlPreferencesRow(expectedBallotId, "D", Some(5), None),
      AtlPreferencesRow(expectedBallotId, "E", Some(6), None)
    )

    assert(expectedPreferences === actualPreferences)
  }

  it should "create the below the line preferences rows as expected" in {
    val (_, _, _, actualPreferences) = formalPreferencesCsvLineToEntities(SenateElection.`2016`, State.ACT, numCandidatesPerGroup, csvLine).get.unapply

    val expectedPreferences = Set(
      BtlPreferencesRow(expectedBallotId, "I", 1, Some(11), None),
      BtlPreferencesRow(expectedBallotId, "C", 1, Some(3), None),
      BtlPreferencesRow(expectedBallotId, "I", 0, None, Some('/')),
      BtlPreferencesRow(expectedBallotId, "B", 1, Some(1), None),
      BtlPreferencesRow(expectedBallotId, "F", 1, Some(7), None),
      BtlPreferencesRow(expectedBallotId, "G", 1, Some(9), None),
      BtlPreferencesRow(expectedBallotId, "A", 0, None, Some('*')),
      BtlPreferencesRow(expectedBallotId, "H", 0, Some(10), None),
      BtlPreferencesRow(expectedBallotId, "G", 0, Some(8), None),
      BtlPreferencesRow(expectedBallotId, "D", 0, Some(4), None),
      BtlPreferencesRow(expectedBallotId, "D", 1, Some(5), None),
      BtlPreferencesRow(expectedBallotId, "J", 0, Some(12), None),
      BtlPreferencesRow(expectedBallotId, "E", 0, Some(6), None),
      BtlPreferencesRow(expectedBallotId, "C", 0, Some(2), None)
    )

    assert(expectedPreferences === actualPreferences)
  }

}
