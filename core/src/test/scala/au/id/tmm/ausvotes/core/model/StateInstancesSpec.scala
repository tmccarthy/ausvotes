package au.id.tmm.ausvotes.core.model

import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class StateInstancesSpec extends ImprovedFlatSpec {

  "ordering by state size" should "be correct" in {
    val expectedOrdering = List(
      State.NSW,
      State.VIC,
      State.QLD,
      State.WA,
      State.SA,
      State.TAS,
      State.ACT,
      State.NT,
    )

    assert(State.ALL_STATES.toList.sorted(StateInstances.orderStatesByPopulation) === expectedOrdering)
  }

}
