package au.id.tmm.senatedb.data.rawdatastore.entityconstruction.formalpreferences

import au.id.tmm.senatedb.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.senatedb.computations.expiry.ExhaustionCalculator
import au.id.tmm.senatedb.data.TestData
import au.id.tmm.senatedb.data.database.model.{AtlPreferencesRow, BallotFactsRow, BallotRow, BtlPreferencesRow}
import au.id.tmm.senatedb.data.rawdatastore.entityconstruction.distributionofpreferences.UsesDopData
import au.id.tmm.senatedb.model.{SenateElection, State}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.collection.immutable.ListMap

class formalPreferencesCsvLineToEntitiesSpec extends ImprovedFlatSpec with UsesDopData {

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

  private val normaliser = BallotNormaliser.forCandidates(TestData.allActCandidates)
  private val exhaustionCalculator = ExhaustionCalculator(TestData.allActCandidates, parsedActCountData)
  private val ballotFactsCalculator = new BallotFactsCalculator(normaliser, exhaustionCalculator)

  private val rawPreferenceParser = new RawPreferenceParser(TestData.actGroupsAndCandidates)

  behaviour of formalPreferencesCsvLineToEntities.getClass.getSimpleName

  it should "create a ballot row as expected" in {
    val (actualBallot, _, _, _) = formalPreferencesCsvLineToEntities(SenateElection.`2016`, State.ACT,
      rawPreferenceParser, ballotFactsCalculator, csvLine).get.unapply

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
    val (_, actualBallotFacts, _, _) = formalPreferencesCsvLineToEntities(SenateElection.`2016`, State.ACT,
      rawPreferenceParser, ballotFactsCalculator, csvLine).get.unapply

    val expectedBallotFacts = BallotFactsRow(expectedBallotId, 6, 12, false, true, None, None)

    assert(actualBallotFacts === expectedBallotFacts)
  }

  it should "create the above the line preferences rows as expected" in {

    val (_, _, actualPreferences, _) = formalPreferencesCsvLineToEntities(SenateElection.`2016`, State.ACT,
      rawPreferenceParser, ballotFactsCalculator, csvLine).get.unapply

    val expectedPreferences = Set(
      AtlPreferencesRow("PQg5+wF5dbfBCEqDyU7tSqv7gxRmYyt3I+LUPoZMfF0=", "G", Some(6), None),
      AtlPreferencesRow("PQg5+wF5dbfBCEqDyU7tSqv7gxRmYyt3I+LUPoZMfF0=", "C", Some(2), None),
      AtlPreferencesRow("PQg5+wF5dbfBCEqDyU7tSqv7gxRmYyt3I+LUPoZMfF0=", "B", Some(3), None),
      AtlPreferencesRow("PQg5+wF5dbfBCEqDyU7tSqv7gxRmYyt3I+LUPoZMfF0=", "J", Some(5), None),
      AtlPreferencesRow("PQg5+wF5dbfBCEqDyU7tSqv7gxRmYyt3I+LUPoZMfF0=", "D", Some(4), None),
      AtlPreferencesRow("PQg5+wF5dbfBCEqDyU7tSqv7gxRmYyt3I+LUPoZMfF0=", "A", Some(1), None)
    )

    assert(expectedPreferences === actualPreferences)
  }

  it should "create the below the line preferences rows as expected" in {
    val (_, _, _, actualPreferences) = formalPreferencesCsvLineToEntities(SenateElection.`2016`, State.ACT,
      rawPreferenceParser, ballotFactsCalculator, csvLine).get.unapply

    val expectedPreferences = Set(
      BtlPreferencesRow("PQg5+wF5dbfBCEqDyU7tSqv7gxRmYyt3I+LUPoZMfF0=", "F", 0,Some(8),None),
      BtlPreferencesRow("PQg5+wF5dbfBCEqDyU7tSqv7gxRmYyt3I+LUPoZMfF0=", "G", 1,Some(11),None),
      BtlPreferencesRow("PQg5+wF5dbfBCEqDyU7tSqv7gxRmYyt3I+LUPoZMfF0=", "J", 0,Some(1),None),
      BtlPreferencesRow("PQg5+wF5dbfBCEqDyU7tSqv7gxRmYyt3I+LUPoZMfF0=", "J", 1,Some(12),None),
      BtlPreferencesRow("PQg5+wF5dbfBCEqDyU7tSqv7gxRmYyt3I+LUPoZMfF0=", "E", 0,Some(4),None),
      BtlPreferencesRow("PQg5+wF5dbfBCEqDyU7tSqv7gxRmYyt3I+LUPoZMfF0=", "C", 0,None,Some('*')),
      BtlPreferencesRow("PQg5+wF5dbfBCEqDyU7tSqv7gxRmYyt3I+LUPoZMfF0=", "UG", 0,Some(7),None),
      BtlPreferencesRow("PQg5+wF5dbfBCEqDyU7tSqv7gxRmYyt3I+LUPoZMfF0=", "B", 0,Some(3),None),
      BtlPreferencesRow("PQg5+wF5dbfBCEqDyU7tSqv7gxRmYyt3I+LUPoZMfF0=", "H", 0,Some(2),None),
      BtlPreferencesRow("PQg5+wF5dbfBCEqDyU7tSqv7gxRmYyt3I+LUPoZMfF0=", "G", 0,Some(5),None),
      BtlPreferencesRow("PQg5+wF5dbfBCEqDyU7tSqv7gxRmYyt3I+LUPoZMfF0=", "A", 0,Some(9),None),
      BtlPreferencesRow("PQg5+wF5dbfBCEqDyU7tSqv7gxRmYyt3I+LUPoZMfF0=", "B", 1,None,Some('/')),
      BtlPreferencesRow("PQg5+wF5dbfBCEqDyU7tSqv7gxRmYyt3I+LUPoZMfF0=", "D", 0,Some(10),None),
      BtlPreferencesRow("PQg5+wF5dbfBCEqDyU7tSqv7gxRmYyt3I+LUPoZMfF0=", "I", 0,Some(6),None)
    )

    assert(expectedPreferences === actualPreferences)
  }

}
