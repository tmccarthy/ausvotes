package au.id.tmm.senatedb.fixtures

import au.id.tmm.senatedb.fixtures.Divisions.DivisionFixture
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.parsing.PollingPlace
import au.id.tmm.utilities.geo.LatLong
import au.id.tmm.utilities.geo.australia.{Address, Postcode, State}

object PollingPlaces {

  trait PollingPlaceFixture {
    val election: SenateElection = SenateElection.`2016`
    def state: State
    def pollingPlaces: Set[PollingPlace]

    def divisionFixture: DivisionFixture

    lazy val divisionLookup = divisionFixture.divisionLookup

    lazy val pollingPlaceLookup = pollingPlaces
      .groupBy(_.name)
      .mapValues(_.head)
  }

  object ACT extends PollingPlaceFixture {
    override val state = State.ACT

    override val divisionFixture = Divisions.ACT

    val BARTON = PollingPlace(
      election = election,
      state = state,
      division = divisionLookup("Canberra"),
      aecId = 8829,
      pollingPlaceType = PollingPlace.Type.POLLING_PLACE,
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
      pollingPlaceType = PollingPlace.Type.OTHER_MOBILE_TEAM,
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
      pollingPlaceType = PollingPlace.Type.SPECIAL_HOSPITAL_TEAM,
      name = "Special Hospital Team 1",
      location = PollingPlace.Location.Multiple
    )

    val WODEN_PRE_POLL = PollingPlace(
      election = election,
      state = state,
      division = divisionLookup("Canberra"),
      aecId = 32705,
      pollingPlaceType = PollingPlace.Type.PRE_POLL_VOTING_CENTRE,
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
}
