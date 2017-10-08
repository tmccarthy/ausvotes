package au.id.tmm.ausvotes.core.rawdata.csv

import au.id.tmm.ausvotes.core.rawdata.model.DistributionOfPreferencesRow
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ParsingDistributionOfPreferencesSpec extends ImprovedFlatSpec {

  it should "correctly parse a distribution of preferences row" in {
    val testCsvRow = Seq("NT","2","102027","34010","1","8"," A","PILE","Jan","6629","6629","6629",
      "1.000000000000000000000000000","","","0",
      "SCULLION, N, McCARTHY, M have been elected to the remaining positions.")

    val actualParsedRow = ParsingDistributionOfPreferences.parseCsvLine(testCsvRow)

    val expectedParsedRow = DistributionOfPreferencesRow(
      state = "NT",
      numberOfVacancies = 2,
      totalFormalPapers = 102027,
      quota = 34010,
      count = 1,
      ballotPosition = 8,
      ticket = " A",
      surname = "PILE",
      givenName = "Jan",
      papers = 6629,
      votesTransferred = 6629,
      progressiveVoteTotal = 6629,
      transferValue = 1.0,
      status = "",
      changed = None,
      orderElected = 0,
      comment = "SCULLION, N, McCARTHY, M have been elected to the remaining positions.")

    assert(actualParsedRow === expectedParsedRow)
  }

}
