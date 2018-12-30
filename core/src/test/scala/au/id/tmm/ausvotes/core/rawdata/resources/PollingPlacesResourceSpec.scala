package au.id.tmm.ausvotes.core.rawdata.resources

import au.id.tmm.ausvotes.model.federal.FederalElection
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class PollingPlacesResourceSpec extends ImprovedFlatSpec {

  "a polling places resource" should "not be supported for the 2013 election" in {
    assert(PollingPlacesResource.of(FederalElection.`2013`) === None)
  }

}
