package au.id.tmm.senatedb.core.tallies

import au.id.tmm.utilities.testing.ImprovedFlatSpec

class Tally0Spec extends ImprovedFlatSpec {

  "a 0 teir tally" can "be added" in {
    assert(Tally0(1) + Tally0(2) === Tally0(3))
  }

  it can "be divided by a double" in {
    assert(Tally0(2) / 2 === Tally0(1))
  }

  it can "not be divided by 0" in {
    intercept[ArithmeticException] {
      Tally0(0) / 0
    }
  }

  it can "be divided by another teir 0 tally" in {
    assert(Tally0(2) / Tally0(2) === Tally0(1))
  }

  it should "accumulate to itself" in {
    assert(Tally0(1).accumulated === Tally0(1))
  }

  it can "be summed" in {
    val actual = Tally0.sum(Vector(
      Tally0(4),
      Tally0(5),
      Tally0(6),
      Tally0(7),
    ))

    assert(actual === Tally0(4 + 5 + 6 + 7))
  }

}
