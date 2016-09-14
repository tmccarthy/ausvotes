package au.id.tmm.senatedb.data.rawdatastore.entityconstruction

import au.id.tmm.senatedb.data.GroupsAndCandidates
import au.id.tmm.senatedb.data.database.model.{CandidatesRow, GroupsRow}
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.io.Source

class parseFirstPreferencesCsvSpec extends ImprovedFlatSpec {

  val testCSVResource = getClass.getResource("firstPreferencesTest.csv")

  val testElection = SenateElection.`2016`

  behaviour of "the parseFirstPreferencesCsv method"

  it should "correctly parse groups from the test csv" in {
    val GroupsAndCandidates(actualGroups, _) = parseFirstPreferencesCsv(testElection, Source.fromURL(testCSVResource)).get

    val expectedGroups = Set(
      GroupsRow("A", testElection.aecID, "ACT", "Liberal Democratic Party"),
      GroupsRow("B", testElection.aecID, "ACT", "Secular Party of Australia"),
      GroupsRow("C", testElection.aecID, "ACT", "Australian Labor Party")
    )

    assert(actualGroups === expectedGroups)
  }

  it should "correctly parse candidates from the test csv" in {
    val GroupsAndCandidates(_, actualCandidates) = parseFirstPreferencesCsv(testElection, Source.fromURL(testCSVResource)).get

    val expectedCandidates = Set(
      CandidatesRow("29611", testElection.aecID, "ACT", "A", 0, "DONNELLY, Matt", "Liberal Democrats"),
      CandidatesRow("29612", testElection.aecID, "ACT", "A", 1, "HENNINGS, Cawley", "Liberal Democrats"),
      CandidatesRow("28933", testElection.aecID, "ACT", "B", 0, "EDWARDS, David", "Secular Party of Australia"),
      CandidatesRow("28937", testElection.aecID, "ACT", "B", 1, "MIHALJEVIC, Denis", "Secular Party of Australia"),
      CandidatesRow("28147", testElection.aecID, "ACT", "C", 0, "GALLAGHER, Katy", "Australian Labor Party"),
      CandidatesRow("28149", testElection.aecID, "ACT", "C", 1, "SMITH, David", "Australian Labor Party")
    )

    assert(expectedCandidates === actualCandidates)
  }

}
