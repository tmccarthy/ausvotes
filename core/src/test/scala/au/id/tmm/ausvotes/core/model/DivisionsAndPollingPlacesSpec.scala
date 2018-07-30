package au.id.tmm.ausvotes.core.model

import au.id.tmm.ausvotes.core.fixtures
import au.id.tmm.ausvotes.core.fixtures.{DivisionFixture, PollingPlaceFixture}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class DivisionsAndPollingPlacesSpec extends ImprovedFlatSpec {

  private val sut = fixtures.DivisionAndPollingPlaceFixture.ACT.divisionsAndPollingPlaces

  "divisions and polling places" should "support division lookup by name" in {
    assert(DivisionFixture.ACT.CANBERRA === sut.lookupDivisionByName("Canberra"))
  }

  it should "support polling place lookup by name" in {
    assert(PollingPlaceFixture.ACT.BARTON === sut.lookupPollingPlaceByName(State.ACT, "Barton"))
  }

  it can "be filtered to a particular election" in {
    val actualFiltered = DivisionsAndPollingPlaces(
      divisions = DivisionFixture.ACT.divisions ++ DivisionFixture.NT.divisions,
      pollingPlaces = PollingPlaceFixture.ACT.pollingPlaces ++ PollingPlaceFixture.NT.pollingPlaces,
    ).findFor(SenateElection.`2016`, State.NT)

    val expectedFiltered = DivisionsAndPollingPlaces(DivisionFixture.NT.divisions, PollingPlaceFixture.NT.pollingPlaces)

    assert(actualFiltered === expectedFiltered)
  }

}
