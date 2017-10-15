package au.id.tmm.ausvotes.core.parsing

import au.id.tmm.ausvotes.core.fixtures.{DivisionFixture, PollingPlaceFixture}
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.flyweights.{DivisionFlyweight, PostcodeFlyweight}
import au.id.tmm.ausvotes.core.model.parsing.PollingPlace
import au.id.tmm.ausvotes.core.rawdata.model.PollingPlacesRow
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class DivisionAndPollingPlaceGenerationSpec extends ImprovedFlatSpec {

  val pollingPlaceWithLocation = PollingPlacesRow("ACT",101,"Canberra",8829,1,"Barton","Telopea Park School",
    "New South Wales Cres","","","BARTON","ACT","2600",Some(-35.3151),Some(149.135))

  val pollingPlaceWithLocationNoLatLong = PollingPlacesRow("ACT",101,"Canberra",65158,4,"Other Mobile Team 1",
    "Alexander Maconachie Centre","10400 Monaro Hwy","","","HUME","ACT","2620", None, None)

  val pollingPlaceWithNoAddressLinesAndLatLong = PollingPlacesRow("ACT",101,"Canberra",32705,5,"Woden CANBERRA PPVC",
    "15 Bowes St","","","","PHILLIP","ACT","2606",Some(-35.344032),Some(149.0860283))

  val pollingPlaceWithMultlipleLocations = PollingPlacesRow("ACT",101,"Canberra",32712,2,"Special Hospital Team 1",
    "Multiple sites","","","","","ACT", "", None, None)

  behaviour of "the polling place generator"

  it should "generate the division" in {
    val actualDivision = DivisionAndPollingPlaceGeneration.fromPollingPlaceRow(SenateElection.`2016`, pollingPlaceWithLocation)
      .division

    assert(DivisionFixture.ACT.CANBERRA === actualDivision)
  }

  it should "generate a polling place with a physical location" in {
    val actual = DivisionAndPollingPlaceGeneration.fromPollingPlaceRow(SenateElection.`2016`, pollingPlaceWithLocation)
      .pollingPlace

    assert(PollingPlaceFixture.ACT.BARTON === actual)
  }

  it should "generate a polling place with an address but no lat/long" in {
    val actual = DivisionAndPollingPlaceGeneration.fromPollingPlaceRow(SenateElection.`2016`, pollingPlaceWithLocationNoLatLong)
      .pollingPlace

    assert(PollingPlaceFixture.ACT.MOBILE_TEAM_1 === actual)
  }

  it should "generate a polling place with no address lines but a lat/long" in {
    val actual = DivisionAndPollingPlaceGeneration.fromPollingPlaceRow(SenateElection.`2016`, pollingPlaceWithNoAddressLinesAndLatLong)
      .pollingPlace

    assert(PollingPlaceFixture.ACT.WODEN_PRE_POLL === actual)
  }

  it should "generate a polling place with multiple locations" in {
    val actual = DivisionAndPollingPlaceGeneration.fromPollingPlaceRow(SenateElection.`2016`, pollingPlaceWithMultlipleLocations)
      .pollingPlace

    assert(PollingPlaceFixture.ACT.HOSPITAL_TEAM_1 === actual)
  }

  it should "flyweight divisions" in {
    val divisionFlyweight = DivisionFlyweight()

    val pollingPlace1 = DivisionAndPollingPlaceGeneration
      .fromPollingPlaceRow(SenateElection.`2016`, pollingPlaceWithLocation, divisionFlyweight)
      .pollingPlace

    val pollingPlace2 = DivisionAndPollingPlaceGeneration
      .fromPollingPlaceRow(SenateElection.`2016`, pollingPlaceWithMultlipleLocations, divisionFlyweight)
      .pollingPlace

    assert(pollingPlace1.division eq pollingPlace2.division)
  }

  it should "flyweight postcodes" in {
    val postcodeFlyweight = PostcodeFlyweight()

    val pollingPlace1 = DivisionAndPollingPlaceGeneration
      .fromPollingPlaceRow(SenateElection.`2016`, pollingPlaceWithLocation, postcodeFlyweight = postcodeFlyweight)
      .pollingPlace

    val pollingPlace2 = DivisionAndPollingPlaceGeneration
      .fromPollingPlaceRow(SenateElection.`2016`, pollingPlaceWithLocation, postcodeFlyweight = postcodeFlyweight)
      .pollingPlace

    val location1 = pollingPlace1.location.asInstanceOf[PollingPlace.Location.Premises]
    val location2 = pollingPlace2.location.asInstanceOf[PollingPlace.Location.Premises]

    assert(location1.address.postcode eq location2.address.postcode)
  }

  it should "be able to handle multiple rows of raw data" in {
    val actualPollingPlaces = DivisionAndPollingPlaceGeneration.fromPollingPlaceRows(SenateElection.`2016`,
      Vector(pollingPlaceWithLocation, pollingPlaceWithMultlipleLocations)).pollingPlaces

    val expectedPollingPlaces = Set(PollingPlaceFixture.ACT.BARTON, PollingPlaceFixture.ACT.HOSPITAL_TEAM_1)

    assert(expectedPollingPlaces === actualPollingPlaces)
  }
}
