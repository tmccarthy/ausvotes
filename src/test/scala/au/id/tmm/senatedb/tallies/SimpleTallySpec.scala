package au.id.tmm.senatedb.tallies

import au.id.tmm.utilities.testing.ImprovedFlatSpec

class SimpleTallySpec extends ImprovedFlatSpec {

  "a simple tally" can "be summed" in {
    assert(SimpleTally(1) + SimpleTally(2) === SimpleTally(3))
  }

  it can "be divided by another" in {
    assert(SimpleTally(2) / SimpleTally(2) === SimpleTally(1))
  }

  it can "be divided by a scalar" in {
    assert(SimpleTally(2) / 2 === SimpleTally(1))
  }

  it can "not be divided by 0" in {
    intercept[ArithmeticException](SimpleTally(2) / 0)
  }
}
