package au.id.tmm.senatedb.core.model

import au.id.tmm.senatedb.core.fixtures
import au.id.tmm.senatedb.core.fixtures.{DivisionFixture, PollingPlaceFixture}
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

}
