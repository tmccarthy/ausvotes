package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.utilities.testing.ImprovedFlatSpec

class Tally2Spec extends ImprovedFlatSpec {

  "a doubly-tiered tally" can "be added" in {
    val left = Tally2("!" -> Tally1("A" -> 1, "B" -> 2), "@" -> Tally1("C" -> 4d))
    val right = Tally2("!" -> Tally1("A" -> 2, "B" -> 3), "@" -> Tally1("C" -> 1d))

    assert(left + right === Tally2("!" -> Tally1("A" -> 3, "B" -> 5), "@" -> Tally1("C" -> 5d)))
  }

  it should "count missing keys as empty when adding" in {
    val left = Tally2("!" -> Tally1("A" -> 1, "B" -> 2), "@" -> Tally1("C" -> 4d))
    val right = Tally2("!" -> Tally1("A" -> 2, "B" -> 3))

    assert(left + right === Tally2("!" -> Tally1("A" -> 3, "B" -> 5), "@" -> Tally1("C" -> 4d)))
  }

  it should "support lookup of the tally for a particular tier" in {
    val tally = Tally2("!" -> Tally1("A" -> 1, "B" -> 2), "@" -> Tally1("C" -> 4d))

    assert(tally("!") === Tally1("A" -> 1, "B" -> 2))
  }
}
