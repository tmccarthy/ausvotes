package au.id.tmm.senatedb.core.tallies

import au.id.tmm.utilities.testing.ImprovedFlatSpec

class Tally1Spec extends ImprovedFlatSpec {

  "a 1 teir tally" can "be added" in {
    val left = Tally1("A" -> 1, "B" -> 2)
    val right = Tally1("A" -> 2, "B" -> 3)

    assert(left + right === Tally1("A" -> 3, "B" -> 5))
  }

  it should "count missing keys as 0 when adding" in {
    val left = Tally1("A" -> 1, "B" -> 2)
    val right = Tally1("B" -> 3d)

    assert(left + right === Tally1("A" -> 1, "B" -> 5))
  }

  it should "support result lookup directly" in {
    val tally = Tally1("A" -> 2, "B" -> 8)

    assert(tally("A") === Tally0(2))
  }
}
