package au.id.tmm.senatedb.core.tallies

import au.id.tmm.utilities.testing.ImprovedFlatSpec

class Tally0Spec extends ImprovedFlatSpec {

  "a 0 teir tally" can "be summed" in {
    assert(Tally0(1) + Tally0(2) === Tally0(3))
  }

}
