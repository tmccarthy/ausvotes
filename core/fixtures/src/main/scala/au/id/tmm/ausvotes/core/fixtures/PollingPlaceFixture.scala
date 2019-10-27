package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.core.fixtures.DivisionFixture.DivisionFixture
import au.id.tmm.ausvotes.model.federal.FederalVoteCollectionPoint.FederalPollingPlace
import au.id.tmm.ausvotes.model.federal.FederalVoteCollectionPoint.FederalPollingPlace.PollingPlaceType
import au.id.tmm.ausvotes.model.federal.{Division, FederalElection}
import au.id.tmm.ausgeo.{Address, Postcode, State, LatLong}
import au.id.tmm.utilities.testing.syntax._

object PollingPlaceFixture {

  trait PollingPlaceFixture {
    val election: FederalElection = FederalElection.`2016`
    def state: State
    def pollingPlaces: Set[FederalPollingPlace]

    def divisionFixture: DivisionFixture

    lazy val divisionLookup: Map[String, Division] = divisionFixture.divisionLookup

    lazy val pollingPlaceLookup: Map[String, FederalPollingPlace] = pollingPlaces
      .groupBy(_.name)
      .view
      .mapValues(_.head)
      .toMap
  }

  object ACT extends PollingPlaceFixture {
    override val state: State = State.ACT

    override val divisionFixture: DivisionFixture.ACT.type = DivisionFixture.ACT

    val BARTON: FederalPollingPlace = FederalPollingPlace(
      election = election,
      state = state,
      division = divisionLookup("Canberra"),
      id = FederalPollingPlace.Id(8829),
      pollingPlaceType = PollingPlaceType.PollingPlace,
      name = "Barton",
      location = FederalPollingPlace.Location.Premises(
        name = "Telopea Park School",
        address = Address(Vector("New South Wales Cres"), "BARTON", Postcode("2600").get, State.ACT),
        location = Some(LatLong(-35.3151, 149.135)),
      ))

    val MOBILE_TEAM_1: FederalPollingPlace = FederalPollingPlace(
      election = election,
      state = state,
      division = divisionLookup("Canberra"),
      id = FederalPollingPlace.Id(65158),
      pollingPlaceType = PollingPlaceType.OtherMobileTeam,
      name = "Other Mobile Team 1",
      location = FederalPollingPlace.Location.Premises(
        name = "Alexander Maconachie Centre",
        address = Address(Vector("10400 Monaro Hwy"), "HUME", Postcode("2620").get, State.ACT),
        location = None,
      ))

    val HOSPITAL_TEAM_1: FederalPollingPlace = FederalPollingPlace(
      election = election,
      state = state,
      division = divisionLookup("Canberra"),
      id = FederalPollingPlace.Id(32712),
      pollingPlaceType = PollingPlaceType.SpecialHospitalTeam,
      name = "Special Hospital Team 1",
      location = FederalPollingPlace.Location.Multiple
    )

    val WODEN_PRE_POLL: FederalPollingPlace = FederalPollingPlace(
      election = election,
      state = state,
      division = divisionLookup("Canberra"),
      id = FederalPollingPlace.Id(32705),
      pollingPlaceType = PollingPlaceType.PrePollVotingCentre,
      name = "Woden CANBERRA PPVC",
      location = FederalPollingPlace.Location.Premises(
        name = "15 Bowes St",
        address = Address(Vector("15 Bowes St"), "PHILLIP", Postcode("2606").get, State.ACT),
        location = Some(LatLong(-35.344032, 149.0860283)),
      )
    )

    val pollingPlaces: Set[FederalPollingPlace] = Set(
      BARTON,
      MOBILE_TEAM_1,
      HOSPITAL_TEAM_1,
      WODEN_PRE_POLL
    )
  }

  object NT extends PollingPlaceFixture {
    override val state: State = State.NT

    override val divisionFixture: DivisionFixture.NT.type = DivisionFixture.NT

    val ALICE_SPRINGS: FederalPollingPlace = FederalPollingPlace(
      election = election,
      state = state,
      division = divisionLookup("Lingiari"),
      id = FederalPollingPlace.Id(8701),
      pollingPlaceType = PollingPlaceType.PollingPlace,
      name = "Alice Springs",
      location = FederalPollingPlace.Location.Premises(
        name = "Alice Springs Civic Centre",
        address = Address(Vector("Cnr Gregory Tce & Todd Street"), "ALICE SPRINGS", Postcode("0870").get, State.NT),
        location = Some(LatLong(-23.7018, 133.882)),
      )
    )

    override val pollingPlaces: Set[FederalPollingPlace] = Set(ALICE_SPRINGS)
  }

  object WA extends PollingPlaceFixture {
    override def state: State = State.WA

    override def divisionFixture: DivisionFixture.WA.type = DivisionFixture.WA

    val ASHFIELD: FederalPollingPlace = FederalPollingPlace(
      election = election,
      state = state,
      division = DivisionFixture.WA.PERTH,
      id = FederalPollingPlace.Id(8127),
      pollingPlaceType = PollingPlaceType.PollingPlace,
      name = "Ashfield",
      location = FederalPollingPlace.Location.Premises(
        name = "Ashfield Primary School",
        address = Address(Vector("65 Margaret St"), "ASHFIELD", Postcode("6054").get, State.WA),
        location = Some(LatLong(-31.9150, 115.9405)),
      )
    )

    override def pollingPlaces: Set[FederalPollingPlace] = Set(
      ASHFIELD,
    )
  }
}
