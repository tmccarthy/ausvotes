package au.id.tmm.senatedb.tallies

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

  it can "be divided by another tally" in {
    val left = TieredTally("!" -> Tally("A" -> 1, "B" -> 2), "@" -> Tally("C" -> 4d))
    val right = TieredTally("!" -> Tally("A" -> 2, "B" -> 4), "@" -> Tally("C" -> 1d))

    assert(left / right === TieredTally("!" -> Tally("A" -> 0.5, "B" -> 0.5), "@" -> Tally("C" -> 4d)))
  }

  it should "count missing keys in the numerator as empty when dividing" in {
    val left = TieredTally("!" -> Tally("A" -> 1, "B" -> 2))
    val right = TieredTally("!" -> Tally("A" -> 2, "B" -> 4), "@" -> Tally("C" -> 1d))

    assert(left / right === TieredTally("!" -> Tally("A" -> 0.5, "B" -> 0.5), "@" -> Tally("C" -> 0d)))
  }

  it should "fail if keys are missing in the denominator" in {
    val left = TieredTally("!" -> Tally("A" -> 1, "B" -> 2), "@" -> Tally("C" -> 4d))
    val right = TieredTally("!" -> Tally("A" -> 2, "B" -> 4))

    intercept[NoSuchElementException](left / right)
  }

  it can "be divided by a scalar" in {
    val tally = TieredTally("!" -> Tally("A" -> 1, "B" -> 2), "@" -> Tally("C" -> 4d))

    assert(tally / 2 === TieredTally("!" -> Tally("A" -> 0.5, "B" -> 1), "@" -> Tally("C" -> 2d)))
  }

  it should "fail if divided by 0" in {
    val tally = TieredTally("!" -> Tally("A" -> 1, "B" -> 2), "@" -> Tally("C" -> 4d))

    intercept[ArithmeticException](tally / 0)
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
