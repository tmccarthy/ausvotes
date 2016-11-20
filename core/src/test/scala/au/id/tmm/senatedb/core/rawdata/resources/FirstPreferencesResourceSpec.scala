package au.id.tmm.senatedb.core.rawdata.resources

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class FirstPreferencesResourceSpec extends ImprovedFlatSpec {

  "a first preferences resource" should "not be supported for the 2013 election" in {
    assert(FirstPreferencesResource.of(SenateElection.`2013`) === None)
  }

}
