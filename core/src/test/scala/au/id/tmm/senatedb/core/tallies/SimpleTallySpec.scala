package au.id.tmm.senatedb.core.tallies

import au.id.tmm.utilities.testing.ImprovedFlatSpec

class SimpleTallySpec extends ImprovedFlatSpec {

  "a simple tally" can "be summed" in {
    assert(SimpleTally(1) + SimpleTally(2) === SimpleTally(3))
  }

}
