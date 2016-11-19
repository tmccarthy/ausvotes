package au.id.tmm.senatedb.rawdata.resources

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class PollingPlacesResourceSpec extends ImprovedFlatSpec {

  "a polling places resource" should "not be supported for the 2013 election" in {
    assert(PollingPlacesResource.of(SenateElection.`2013`) === None)
  }

}
