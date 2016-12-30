package au.id.tmm.senatedb.core.model.parsing

import au.id.tmm.senatedb.core.fixtures.PollingPlaces
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class VoteCollectionPointSpec extends ImprovedFlatSpec {

  "a vote collection point" should "have an address if it is a polling place with a single premises" in {
    val pollingPlace = PollingPlaces.ACT.BARTON

    val expectedAddress = PollingPlaces.ACT.BARTON
      .location
      .asInstanceOf[PollingPlace.Location.Premises]
      .address

    assert(VoteCollectionPoint.addressOf(pollingPlace) === Some(expectedAddress))
  }

  it should "have an address if it is a polling place with multiple premises" in {
    val pollingPlace = PollingPlaces.ACT.MOBILE_TEAM_1

    val expectedAddress = PollingPlaces.ACT.MOBILE_TEAM_1
      .location
      .asInstanceOf[PollingPlace.Location.PremisesMissingLatLong]
      .address

    assert(VoteCollectionPoint.addressOf(pollingPlace) === Some(expectedAddress))
  }

  it should "not have an address if it is a polling place without an address" in {
    val pollingPlace = PollingPlaces.ACT.HOSPITAL_TEAM_1

    assert(VoteCollectionPoint.addressOf(pollingPlace) === None)
  }

}
