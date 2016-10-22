package au.id.tmm.senatedb.reporting.totalformal

import au.id.tmm.senatedb.fixtures.{Divisions, PollingPlaces}
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.parsing.Party
import au.id.tmm.senatedb.reporting.TallyReport
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class TotalFormalBallotsReportSpec extends ImprovedFlatSpec {

  "a TotalFormalBallotsReport" should "accumulate by adding the respective totals" in {
    val left = TallyReport(
      total = 5,
      perState = Map(State.ACT -> 5l),
      perDivision = Map(Divisions.ACT.CANBERRA -> 5l),
      perVoteCollectionPlace = Map(PollingPlaces.ACT.BARTON -> 5l),
      perFirstPreferencedParty = Map(Some(Party(SenateElection.`2016`, "ALP")) -> 5l)
    )

    val right = TallyReport(
      total = 6,
      perState = Map(State.ACT -> 6l),
      perDivision = Map(Divisions.ACT.CANBERRA -> 6l),
      perVoteCollectionPlace = Map(PollingPlaces.ACT.BARTON -> 6l),
      perFirstPreferencedParty = Map(Some(Party(SenateElection.`2016`, "Liberal")) -> 6l)
    )

    val expected = TallyReport(
      total = 11,
      perState = Map(State.ACT -> 11l),
      perDivision = Map(Divisions.ACT.CANBERRA -> 11l),
      perVoteCollectionPlace = Map(PollingPlaces.ACT.BARTON -> 11l),
      perFirstPreferencedParty = Map(Some(Party(SenateElection.`2016`, "ALP")) -> 5l, Some(Party(SenateElection.`2016`, "Liberal")) -> 6l)
    )

    assert((left accumulate right) === expected)
  }

}
