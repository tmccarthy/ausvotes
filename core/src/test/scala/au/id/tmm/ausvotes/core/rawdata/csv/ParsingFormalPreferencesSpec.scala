package au.id.tmm.ausvotes.core.rawdata.csv

import au.id.tmm.ausvotes.core.rawdata.model.FormalPreferencesRow
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ParsingFormalPreferencesSpec extends ImprovedFlatSpec {

  it should "correctly parse a formal preferences row" in {
    val testCsvRow = Seq("Canberra","Barton","1","1","1","4,,3,,,1,5,2,6,,,,,,,,,,,,,,,,,,,,,,,")

    val actualParsedRow = ParsingFormalPreferences.parseCsvLine(testCsvRow)

    val expectedParsedRow = FormalPreferencesRow(
      electorateName = "Canberra",
      voteCollectionPointName = "Barton",
      voteCollectionPointId = 1,
      batchNumber = 1,
      paperNumber = 1,
      preferences = "4,,3,,,1,5,2,6,,,,,,,,,,,,,,,,,,,,,,,"
    )

    assert(actualParsedRow === expectedParsedRow)
  }

}
