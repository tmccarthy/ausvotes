package au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl

import au.id.tmm.ausvotes.core.fixtures.{DivisionFixture, PollingPlaceFixture}
import au.id.tmm.ausvotes.data_sources.aec.federal.raw.FetchRawFederalPollingPlaces
import au.id.tmm.ausvotes.model.federal.FederalElection
import au.id.tmm.ausvotes.shared.io.test.BasicTestData
import au.id.tmm.ausvotes.shared.io.test.BasicTestData.BasicTestIO
import au.id.tmm.bfect.BME
import au.id.tmm.bfect.fs2interop._
import org.scalatest.FlatSpec
import fs2.Stream

class FetchDivisionsAndFederalPollingPlacesFromRawSpec extends FlatSpec {

  private val pollingPlaceWithLocation = FetchRawFederalPollingPlaces.Row("ACT",101,"Canberra",8829,1,"Barton","Telopea Park School",
    "New South Wales Cres","","","BARTON","ACT","2600",Some(-35.3151),Some(149.135))

  private val pollingPlaceWithLocationNoLatLong = FetchRawFederalPollingPlaces.Row("ACT",101,"Canberra",65158,4,"Other Mobile Team 1",
    "Alexander Maconachie Centre","10400 Monaro Hwy","","","HUME","ACT","2620", None, None)

  private val pollingPlaceWithNoAddressLinesAndLatLong = FetchRawFederalPollingPlaces.Row("ACT",101,"Canberra",32705,5,"Woden CANBERRA PPVC",
    "15 Bowes St","","","","PHILLIP","ACT","2606",Some(-35.344032),Some(149.0860283))

  private val pollingPlaceWithMultlipleLocations = FetchRawFederalPollingPlaces.Row("ACT",101,"Canberra",32712,2,"Special Hospital Team 1",
    "Multiple sites","","","","","ACT", "", None, None)

  private implicit val fetchRawFederalPollingPlaces: FetchRawFederalPollingPlaces[BasicTestIO] =
  {
    case FederalElection.`2016` => BME.pure[BasicTestIO, Stream[BasicTestIO[Throwable, +?], FetchRawFederalPollingPlaces.Row]](Stream(
      pollingPlaceWithLocation,
      pollingPlaceWithMultlipleLocations,
    ))
    case _ => BME.leftPure[BasicTestIO, FetchRawFederalPollingPlaces.Error](FetchRawFederalPollingPlaces.Error(new RuntimeException("No data")))
  }

  private val fetcherUnderTest: FetchDivisionsAndFederalPollingPlacesFromRaw[BasicTestIO] =
    FetchDivisionsAndFederalPollingPlacesFromRaw[BasicTestIO]

  behaviour of "the polling place generator"

  it should "be able to handle multiple rows of raw data" in {
    val actualPollingPlaces = fetcherUnderTest.divisionsAndFederalPollingPlacesFor(FederalElection.`2016`)
      .runEither(BasicTestData())
      .map(_.pollingPlaces)

    val expectedPollingPlaces = Set(PollingPlaceFixture.ACT.BARTON, PollingPlaceFixture.ACT.HOSPITAL_TEAM_1)

    assert(actualPollingPlaces === Right(expectedPollingPlaces))
  }

  it should "generate the division" in {
    val actualDivision = fetcherUnderTest.parseRawRow(FederalElection.`2016`, pollingPlaceWithLocation)
      .map(_.division)

    assert(actualDivision === Right(DivisionFixture.ACT.CANBERRA))
  }

  it should "generate a polling place with a physical location" in {
    val actual = fetcherUnderTest.parseRawRow(FederalElection.`2016`, pollingPlaceWithLocation)
      .map(_.pollingPlace)

    assert(actual === Right(PollingPlaceFixture.ACT.BARTON))
  }

  it should "generate a polling place with an address but no lat/long" in {
    val actual = fetcherUnderTest.parseRawRow(FederalElection.`2016`, pollingPlaceWithLocationNoLatLong)
      .map(_.pollingPlace)

    assert(actual === Right(PollingPlaceFixture.ACT.MOBILE_TEAM_1))
  }

  it should "generate a polling place with no address lines but a lat/long" in {
    val actual = fetcherUnderTest.parseRawRow(FederalElection.`2016`, pollingPlaceWithNoAddressLinesAndLatLong)
      .map(_.pollingPlace)

    assert(actual === Right(PollingPlaceFixture.ACT.WODEN_PRE_POLL))
  }

  it should "generate a polling place with multiple locations" in {
    val actual = fetcherUnderTest.parseRawRow(FederalElection.`2016`, pollingPlaceWithMultlipleLocations)
      .map(_.pollingPlace)

    assert(actual === Right(PollingPlaceFixture.ACT.HOSPITAL_TEAM_1))
  }

  it should "flyweight divisions" in {
    val pollingPlace1 = fetcherUnderTest.parseRawRow(FederalElection.`2016`, pollingPlaceWithLocation)
      .map(_.pollingPlace)

    val pollingPlace2 = fetcherUnderTest.parseRawRow(FederalElection.`2016`, pollingPlaceWithMultlipleLocations)
      .map(_.pollingPlace)

    {
      for {
        division1 <- pollingPlace1.map(_.division)
        division2 <- pollingPlace2.map(_.division)
      } yield assert(division1 eq division2)
    }.getOrElse(fail())
  }

}
