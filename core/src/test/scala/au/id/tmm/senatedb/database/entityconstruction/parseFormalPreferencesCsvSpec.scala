package au.id.tmm.senatedb.database.entityconstruction

import au.id.tmm.senatedb.database.model.{AtlPreferencesRow, BallotRow, BtlPreferencesRow}
import au.id.tmm.senatedb.model.{SenateElection, State}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.io.Source

class parseFormalPreferencesCsvSpec extends ImprovedFlatSpec {
  val testCSVResource = getClass.getResource("formalPreferencesTest.csv")

  val testElection = SenateElection.`2016`
  val testState = State.ACT
  val allCandidates = EntityConstructionSpecUtils.allActCandidates

  behaviour of "the parseFormalPreferencesCsv method"

  it should "correctly parse ballots from the test csv" in {
    val (actualBallots, _, _) = parseFormalPreferencesCsv(testElection, testState, allCandidates, Source.fromURL(testCSVResource)).get

    val expectedBallots = Set(
      BallotRow("PSvN2Cq2X9pO8x3Pye6MZq/b9fh5CkLvchPmsRsg1Js=", "20499", "ACT", "Canberra", 1, 1, 1, true, -1),
      BallotRow("XrAV7MhxzOjz2scfHbK6JpWg2/VgYZayhGzGY9k35Lw=", "20499", "ACT", "Canberra", 1, 1, 2, true, -1),
      BallotRow("lXBpzxIh0wCXehBHs0G7iE4Lu0Wxe2jOtf9k4Gzs3jI=", "20499", "ACT", "Canberra", 1, 1, 3, true, -1),
      BallotRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "20499", "ACT", "Canberra", 1, 36, 1, true, -1)
    )

    assert(actualBallots === expectedBallots)
  }

  it should "correctly parse above the line preferences from the test csv" in {
    val (_, actualAtlPreferences, _) = parseFormalPreferencesCsv(testElection, testState, allCandidates, Source.fromURL(testCSVResource)).get

    val expectedAtlPreferences = Set(
      AtlPreferencesRow("lXBpzxIh0wCXehBHs0G7iE4Lu0Wxe2jOtf9k4Gzs3jI=", "H", 5),
      AtlPreferencesRow("PSvN2Cq2X9pO8x3Pye6MZq/b9fh5CkLvchPmsRsg1Js=", "UG", 5),
      AtlPreferencesRow("XrAV7MhxzOjz2scfHbK6JpWg2/VgYZayhGzGY9k35Lw=", "UG", 4),
      AtlPreferencesRow("XrAV7MhxzOjz2scfHbK6JpWg2/VgYZayhGzGY9k35Lw=", "E", 3),
      AtlPreferencesRow("PSvN2Cq2X9pO8x3Pye6MZq/b9fh5CkLvchPmsRsg1Js=", "F", 3),
      AtlPreferencesRow("PSvN2Cq2X9pO8x3Pye6MZq/b9fh5CkLvchPmsRsg1Js=", "C", 6),
      AtlPreferencesRow("XrAV7MhxzOjz2scfHbK6JpWg2/VgYZayhGzGY9k35Lw=", "I", 5),
      AtlPreferencesRow("XrAV7MhxzOjz2scfHbK6JpWg2/VgYZayhGzGY9k35Lw=", "F", 6),
      AtlPreferencesRow("PSvN2Cq2X9pO8x3Pye6MZq/b9fh5CkLvchPmsRsg1Js=", "G", 1),
      AtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "D", 2),
      AtlPreferencesRow("PSvN2Cq2X9pO8x3Pye6MZq/b9fh5CkLvchPmsRsg1Js=", "B", 2),
      AtlPreferencesRow("XrAV7MhxzOjz2scfHbK6JpWg2/VgYZayhGzGY9k35Lw=", "G", 1),
      AtlPreferencesRow("lXBpzxIh0wCXehBHs0G7iE4Lu0Wxe2jOtf9k4Gzs3jI=", "I", 4),
      AtlPreferencesRow("lXBpzxIh0wCXehBHs0G7iE4Lu0Wxe2jOtf9k4Gzs3jI=", "J", 2),
      AtlPreferencesRow("lXBpzxIh0wCXehBHs0G7iE4Lu0Wxe2jOtf9k4Gzs3jI=", "UG", 6),
      AtlPreferencesRow("lXBpzxIh0wCXehBHs0G7iE4Lu0Wxe2jOtf9k4Gzs3jI=", "E", 3),
      AtlPreferencesRow("PSvN2Cq2X9pO8x3Pye6MZq/b9fh5CkLvchPmsRsg1Js=", "E", 4),
      AtlPreferencesRow("XrAV7MhxzOjz2scfHbK6JpWg2/VgYZayhGzGY9k35Lw=", "H", 2),
      AtlPreferencesRow("lXBpzxIh0wCXehBHs0G7iE4Lu0Wxe2jOtf9k4Gzs3jI=", "G", 1)
    )

    assert(expectedAtlPreferences === actualAtlPreferences)
  }

  it should "correctly parse below the line preferences from the test csv" in {
    val (_, _, actualBtlPreferences) = parseFormalPreferencesCsv(testElection, testState, allCandidates, Source.fromURL(testCSVResource)).get

    val expectedBtlPreferences = Set(
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "A", 0, 8),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "H", 1, 17),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "J", 1, 1),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "C", 1, 16),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "J", 0, 4),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "F", 1, 7),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "B", 0, 18),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "E", 1, 5),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "I", 0, 10),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "UG", 1, 14),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "G", 1, 12),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "A", 1, 9),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "UG", 0, 13),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "E", 0, 3),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "D", 0, 21),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "B", 1, 15),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "C", 0, 19),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "I", 1, 22),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "F", 0, 6),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "H", 0, 20),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "G", 0, 11)
    )

    assert(expectedBtlPreferences === actualBtlPreferences)
  }

}
