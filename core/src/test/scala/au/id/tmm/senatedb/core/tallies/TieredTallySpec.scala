package au.id.tmm.senatedb.core.tallies

import au.id.tmm.utilities.testing.ImprovedFlatSpec

class TieredTallySpec extends ImprovedFlatSpec {

  "a doubly-tiered tally" can "be added" in {
    val left = TieredTally("!" -> Tally("A" -> 1, "B" -> 2), "@" -> Tally("C" -> 4d))
    val right = TieredTally("!" -> Tally("A" -> 2, "B" -> 3), "@" -> Tally("C" -> 1d))

    assert(left + right === TieredTally("!" -> Tally("A" -> 3, "B" -> 5), "@" -> Tally("C" -> 5d)))
  }

  it should "count missing keys as empty when adding" in {
    val left = TieredTally("!" -> Tally("A" -> 1, "B" -> 2), "@" -> Tally("C" -> 4d))
    val right = TieredTally("!" -> Tally("A" -> 2, "B" -> 3))

    assert(left + right === TieredTally("!" -> Tally("A" -> 3, "B" -> 5), "@" -> Tally("C" -> 4d)))
  }

  it can "be built by incrementing keys by 1" in {
    val builder = TieredTally.Builder[String, String]()

    builder.increment("!", "A")
    builder.increment("!", "A")
    builder.increment("@", "B")

    val tally = builder.build()

    val expected = TieredTally(
      "!" -> Tally("A" -> 2d),
      "@" -> Tally("B" -> 1d)
    )

    assert(tally === expected)
  }

  it can "be built by incrementing keys by any amount" in {
    val builder = TieredTally.Builder[String, String]()

    builder.incrementBy("!", "A", 3)
    builder.incrementBy("@", "B", 1)

    val tally = builder.build()

    val expected = TieredTally(
      "!" -> Tally("A" -> 3d),
      "@" -> Tally("B" -> 1d)
    )

    assert(tally === expected)
  }

  it should "support lookup of the tally for a particular tier" in {
    val tally = TieredTally("!" -> Tally("A" -> 1, "B" -> 2), "@" -> Tally("C" -> 4d))

    assert(tally("!") === Tally("A" -> 1, "B" -> 2))
  }
}
