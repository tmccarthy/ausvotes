package au.id.tmm.senatedb.data.rawdatastore.entityconstruction

import au.id.tmm.senatedb.data.TestData
import au.id.tmm.senatedb.data.database.{AtlPreferencesRow, BallotRow, BtlPreferencesRow}
import au.id.tmm.senatedb.model.{SenateElection, State}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.io.Source

class parseFormalPreferencesCsvSpec extends ImprovedFlatSpec {
  val testCSVResource = getClass.getResource("formalPreferencesTest.csv")

  val testElection = SenateElection.`2016`
  val testState = State.ACT
  val allCandidates = TestData.allActCandidates

  behaviour of "the parseFormalPreferencesCsv method"

  it should "correctly parse ballots from the test csv" in {
    val actualBallots = parseFormalPreferencesCsv(testElection, testState, allCandidates, Source.fromURL(testCSVResource)).get
      .map(_.ballot)
      .toSet

    val expectedBallots = Set(
      BallotRow("PSvN2Cq2X9pO8x3Pye6MZq/b9fh5CkLvchPmsRsg1Js=", "20499", "ACT", "Canberra", 1, 1, 1),
      BallotRow("XrAV7MhxzOjz2scfHbK6JpWg2/VgYZayhGzGY9k35Lw=", "20499", "ACT", "Canberra", 1, 1, 2),
      BallotRow("lXBpzxIh0wCXehBHs0G7iE4Lu0Wxe2jOtf9k4Gzs3jI=", "20499", "ACT", "Canberra", 1, 1, 3),
      BallotRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "20499", "ACT", "Canberra", 1, 36, 1)
    )

    assert(actualBallots === expectedBallots)
  }

  it should "correctly parse above the line preferences from the test csv" in {
    val actualAtlPreferences = parseFormalPreferencesCsv(testElection, testState, allCandidates, Source.fromURL(testCSVResource)).get
      .map(_.atlPreferences)
      .flatten
      .toSet

    val expectedAtlPreferences = Set(
      AtlPreferencesRow("lXBpzxIh0wCXehBHs0G7iE4Lu0Wxe2jOtf9k4Gzs3jI=", "H", Some(5), None),
      AtlPreferencesRow("PSvN2Cq2X9pO8x3Pye6MZq/b9fh5CkLvchPmsRsg1Js=", "UG", Some(5), None),
      AtlPreferencesRow("XrAV7MhxzOjz2scfHbK6JpWg2/VgYZayhGzGY9k35Lw=", "UG", Some(4), None),
      AtlPreferencesRow("XrAV7MhxzOjz2scfHbK6JpWg2/VgYZayhGzGY9k35Lw=", "E", Some(3), None),
      AtlPreferencesRow("PSvN2Cq2X9pO8x3Pye6MZq/b9fh5CkLvchPmsRsg1Js=", "F", Some(3), None),
      AtlPreferencesRow("PSvN2Cq2X9pO8x3Pye6MZq/b9fh5CkLvchPmsRsg1Js=", "C", Some(6), None),
      AtlPreferencesRow("XrAV7MhxzOjz2scfHbK6JpWg2/VgYZayhGzGY9k35Lw=", "I", Some(5), None),
      AtlPreferencesRow("XrAV7MhxzOjz2scfHbK6JpWg2/VgYZayhGzGY9k35Lw=", "F", Some(6), None),
      AtlPreferencesRow("PSvN2Cq2X9pO8x3Pye6MZq/b9fh5CkLvchPmsRsg1Js=", "G", Some(1), None),
      AtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "D", Some(2), None),
      AtlPreferencesRow("PSvN2Cq2X9pO8x3Pye6MZq/b9fh5CkLvchPmsRsg1Js=", "B", Some(2), None),
      AtlPreferencesRow("XrAV7MhxzOjz2scfHbK6JpWg2/VgYZayhGzGY9k35Lw=", "G", Some(1), None),
      AtlPreferencesRow("lXBpzxIh0wCXehBHs0G7iE4Lu0Wxe2jOtf9k4Gzs3jI=", "I", Some(4), None),
      AtlPreferencesRow("lXBpzxIh0wCXehBHs0G7iE4Lu0Wxe2jOtf9k4Gzs3jI=", "J", Some(2), None),
      AtlPreferencesRow("lXBpzxIh0wCXehBHs0G7iE4Lu0Wxe2jOtf9k4Gzs3jI=", "UG", Some(6), None),
      AtlPreferencesRow("lXBpzxIh0wCXehBHs0G7iE4Lu0Wxe2jOtf9k4Gzs3jI=", "E", Some(3), None),
      AtlPreferencesRow("PSvN2Cq2X9pO8x3Pye6MZq/b9fh5CkLvchPmsRsg1Js=", "E", Some(4), None),
      AtlPreferencesRow("XrAV7MhxzOjz2scfHbK6JpWg2/VgYZayhGzGY9k35Lw=", "H", Some(2), None),
      AtlPreferencesRow("lXBpzxIh0wCXehBHs0G7iE4Lu0Wxe2jOtf9k4Gzs3jI=", "G",Some(1), None)
    )

    assert(expectedAtlPreferences === actualAtlPreferences)
  }

  it should "correctly parse below the line preferences from the test csv" in {
    val actualBtlPreferences = parseFormalPreferencesCsv(testElection, testState, allCandidates, Source.fromURL(testCSVResource)).get
      .map(_.btlPreferences)
      .flatten
      .toSet

    val expectedBtlPreferences = Set(
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "A", 0, Some(8), None),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "H", 1, Some(17), None),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "J", 1, Some(1), None),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "C", 1, Some(16), None),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "J", 0, Some(4), None),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "F", 1, Some(7), None),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "B", 0, Some(18), None),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "E", 1, Some(5), None),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "I", 0, Some(10), None),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "UG", 1, Some(14), None),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "G", 1, Some(12), None),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "A", 1, Some(9), None),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "UG", 0, Some(13), None),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "E", 0, Some(3), None),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "D", 0, Some(21), None),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "B", 1, Some(15), None),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "C", 0, Some(19), None),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "I", 1, Some(22), None),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "F", 0, Some(6), None),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "H", 0, Some(20), None),
      BtlPreferencesRow("HXZMA9dj6CrTdOKs06Jq8v1hvXW3N3B2zzodSNsFpxk=", "G", 0, Some(11), None)
    )

    assert(expectedBtlPreferences === actualBtlPreferences)
  }

}
