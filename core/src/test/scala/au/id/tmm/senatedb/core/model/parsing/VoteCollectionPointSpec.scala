package au.id.tmm.senatedb.core.model.parsing

import au.id.tmm.senatedb.core.fixtures.PollingPlaceFixture
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class VoteCollectionPointSpec extends ImprovedFlatSpec {

  "a vote collection point" should "have an address if it is a polling place with a single premises" in {
    val pollingPlace = PollingPlaceFixture.ACT.BARTON

    val expectedAddress = PollingPlaceFixture.ACT.BARTON
      .location
      .asInstanceOf[PollingPlace.Location.Premises]
      .address

    assert(VoteCollectionPoint.addressOf(pollingPlace) === Some(expectedAddress))
  }

  it should "have an address if it is a polling place with multiple premises" in {
    val pollingPlace = PollingPlaceFixture.ACT.MOBILE_TEAM_1

    val expectedAddress = PollingPlaceFixture.ACT.MOBILE_TEAM_1
      .location
      .asInstanceOf[PollingPlace.Location.PremisesMissingLatLong]
      .address

    assert(VoteCollectionPoint.addressOf(pollingPlace) === Some(expectedAddress))
  }

  it should "not have an address if it is a polling place without an address" in {
    val pollingPlace = PollingPlaceFixture.ACT.HOSPITAL_TEAM_1

    assert(VoteCollectionPoint.addressOf(pollingPlace) === None)
  }

}
