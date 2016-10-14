package au.id.tmm.senatedb.rawdata.csv

import au.id.tmm.senatedb.rawdata.model.PollingPlacesRow
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ParsingPollingPlacesSpec extends ImprovedFlatSpec {
  it should "correctly parse a first preferences row" in {
    val testCsvRow = Seq("ACT","101","Canberra","8829","1","Barton","Telopea Park School","New South Wales Cres","","",
      "BARTON","ACT","2600","-35.3151","149.135")

    val actualParsedRow = ParsingPollingPlaces.parseCsvLine(testCsvRow)

    val expectedParsedRow = PollingPlacesRow(
      "ACT", 101, "Canberra", 8829, 1, "Barton", "Telopea Park School", "New South Wales Cres", "", "", "BARTON", "ACT",
      "2600", Some(-35.3151), Some(149.135)
    )

    assert(actualParsedRow === expectedParsedRow)
  }

  it should "correctly parse a row without lat/long" in {
    val testCsvRow = Seq("ACT","101","Canberra","8829","1","Barton","Telopea Park School","New South Wales Cres","","",
      "BARTON","ACT","2600","","")

    val actualParsedRow = ParsingPollingPlaces.parseCsvLine(testCsvRow)

    val expectedParsedRow = PollingPlacesRow(
      "ACT", 101, "Canberra", 8829, 1, "Barton", "Telopea Park School", "New South Wales Cres", "", "", "BARTON", "ACT",
      "2600", None, None
    )

    assert(actualParsedRow === expectedParsedRow)
  }
}
