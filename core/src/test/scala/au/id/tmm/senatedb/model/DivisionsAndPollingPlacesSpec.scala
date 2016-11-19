package au.id.tmm.senatedb.model

import au.id.tmm.senatedb.fixtures
import au.id.tmm.senatedb.fixtures.{Divisions, PollingPlaces}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class DivisionsAndPollingPlacesSpec extends ImprovedFlatSpec {

  private val sut = fixtures.DivisionsAndPollingPlaces.ACT.divisionsAndPollingPlaces

  "divisions and polling places" should "support division lookup by name" in {
    assert(Divisions.ACT.CANBERRA === sut.lookupDivisionByName("Canberra"))
  }

  it should "support polling place lookup by name" in {
    assert(PollingPlaces.ACT.BARTON === sut.lookupPollingPlaceByName(State.ACT, "Barton"))
  }

}
