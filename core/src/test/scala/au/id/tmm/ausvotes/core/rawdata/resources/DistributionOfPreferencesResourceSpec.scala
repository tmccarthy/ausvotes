package au.id.tmm.ausvotes.core.rawdata.resources

import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class DistributionOfPreferencesResourceSpec extends ImprovedFlatSpec {

  "a distribution of preferences resource" should "not be supported for the 2013 election" in {
    assert(DistributionOfPreferencesResource.of(SenateElection.`2013`) === None)
  }

}
