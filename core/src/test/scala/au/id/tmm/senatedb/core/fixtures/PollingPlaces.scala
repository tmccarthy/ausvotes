package au.id.tmm.senatedb.core.fixtures

import au.id.tmm.senatedb.core.fixtures.Divisions.DivisionFixture
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.{Division, PollingPlace}
import au.id.tmm.senatedb.core.model.parsing.PollingPlace.PollingPlaceType
import au.id.tmm.utilities.geo.LatLong
import au.id.tmm.utilities.geo.australia.{Address, Postcode, State}

object PollingPlaces {

  trait PollingPlaceFixture {
    val election: SenateElection = SenateElection.`2016`
    def state: State
    def pollingPlaces: Set[PollingPlace]

    def divisionFixture: DivisionFixture

    lazy val divisionLookup: Map[String, Division] = divisionFixture.divisionLookup

    lazy val pollingPlaceLookup: Map[String, PollingPlace] = pollingPlaces
      .groupBy(_.name)
      .mapValues(_.head)
  }

  object ACT extends PollingPlaceFixture {
    override val state: State = State.ACT

    override val divisionFixture: Divisions.ACT.type = Divisions.ACT

    val BARTON = PollingPlace(
      election = election,
      state = state,
      division = divisionLookup("Canberra"),
      aecId = 8829,
      pollingPlaceType = PollingPlaceType.PollingPlace,
      name = "Barton",
      location = PollingPlace.Location.Premises(
        name = "Telopea Park School",
        address = Address(Vector("New South Wales Cres"), "BARTON", Postcode("2600"), State.ACT),
        location = LatLong(-35.3151, 149.135)
      ))

    val MOBILE_TEAM_1 = PollingPlace(
      election = election,
      state = state,
      division = divisionLookup("Canberra"),
      aecId = 65158,
      pollingPlaceType = PollingPlaceType.OtherMobileTeam,
      name = "Other Mobile Team 1",
      location = PollingPlace.Location.PremisesMissingLatLong(
        name = "Alexander Maconachie Centre",
        address = Address(Vector("10400 Monaro Hwy"), "HUME", Postcode("2620"), State.ACT)
      ))

    val HOSPITAL_TEAM_1 = PollingPlace(
      election = election,
      state = state,
      division = divisionLookup("Canberra"),
      aecId = 32712,
      pollingPlaceType = PollingPlaceType.SpecialHospitalTeam,
      name = "Special Hospital Team 1",
      location = PollingPlace.Location.Multiple
    )

    val WODEN_PRE_POLL = PollingPlace(
      election = election,
      state = state,
      division = divisionLookup("Canberra"),
      aecId = 32705,
      pollingPlaceType = PollingPlaceType.PrePollVotingCentre,
      name = "Woden CANBERRA PPVC",
      location = PollingPlace.Location.Premises(
        name = "15 Bowes St",
        address = Address(Vector("15 Bowes St"), "PHILLIP", Postcode("2606"), State.ACT),
        location = LatLong(-35.344032, 149.0860283)
      )
    )

    val pollingPlaces = Set(
      BARTON,
      MOBILE_TEAM_1,
      HOSPITAL_TEAM_1,
      WODEN_PRE_POLL
    )
  }

  object NT extends PollingPlaceFixture {
    override val state: State = State.NT

    override val divisionFixture: Divisions.NT.type = Divisions.NT

    val ALICE_SPRINGS = PollingPlace(
      election = election,
      state = state,
      division = divisionLookup("Lingiari"),
      aecId = 8701,
      pollingPlaceType = PollingPlaceType.PollingPlace,
      name = "Alice Springs",
      location = PollingPlace.Location.Premises(
        name = "Alice Springs Civic Centre",
        address = Address(Vector("Cnr Gregory Tce & Todd Street"), "ALICE SPRINGS", Postcode("0870"), State.NT),
        location = LatLong(-23.7018, 133.882)
      )
    )

    override val pollingPlaces = Set(ALICE_SPRINGS)
  }
}
