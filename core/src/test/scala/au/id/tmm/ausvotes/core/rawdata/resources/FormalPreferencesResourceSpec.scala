package au.id.tmm.ausvotes.core.rawdata.resources

import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, SenateElectionForState}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class FormalPreferencesResourceSpec extends ImprovedFlatSpec {

  "a unique formal preferences resource" should "exist for every state and territory in the 2016 election" in {
    val all2016FormalPrefResources = State.ALL_STATES.map(FormalPreferencesResource.for2016)

    assert(all2016FormalPrefResources.map(_.state) === State.ALL_STATES)
  }

  "formal preferences resources" should "not be supported for the 2013 election" in {
    assert(FormalPreferencesResource.of(SenateElectionForState(SenateElection.`2013`, State.NSW).right.get) === None)
  }
}
