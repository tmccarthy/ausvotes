package au.id.tmm.senatedb.rawdata.csv

import au.id.tmm.senatedb.rawdata.model.FirstPreferencesRow
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ParsingFirstPreferencesSpec extends ImprovedFlatSpec {
  it should "correctly parse a first preferences row" in {
    val testCsvRow = Seq("ACT","A","29611","1","DONNELLY, Matt","Liberal Democrats","778","16","10","42","38","884")

    val actualParsedRow = ParsingFirstPreferences.parseCsvLine(testCsvRow)

    val expectedParsedRow = FirstPreferencesRow(
      state = "ACT",
      ticket = "A",
      candidateId = "29611",
      ballotPosition = 1,
      candidateDetails = "DONNELLY, Matt",
      party = "Liberal Democrats",
      ordinaryVotes = 778,
      absentVotes = 16,
      provisionalVotes = 10,
      prePollVotes = 42,
      postalVotes = 38,
      totalVotes = 38
    )

    assert(actualParsedRow === expectedParsedRow)
  }
}
