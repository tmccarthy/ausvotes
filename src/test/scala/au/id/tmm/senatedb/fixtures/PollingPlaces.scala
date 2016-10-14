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

    val pollingPlaces = Set(
      PollingPlace(
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
    )

    val BARTON: PollingPlace = pollingPlaceLookup("Barton")
  }
}
