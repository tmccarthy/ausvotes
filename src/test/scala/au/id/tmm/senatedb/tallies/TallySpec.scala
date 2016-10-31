package au.id.tmm.senatedb.tallies

import au.id.tmm.senatedb.tallies.Tally.MapOps
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class TallySpec extends ImprovedFlatSpec {

  "a tally" can "be added" in {
    val left = Tally("A" -> 1, "B" -> 2)
    val right = Tally("A" -> 2, "B" -> 3)

    assert(left + right === Tally("A" -> 3, "B" -> 5))
  }

  it should "count missing keys as 0 when adding" in {
    val left = Tally("A" -> 1, "B" -> 2)
    val right = Tally("B" -> 3d)

    assert(left + right === Tally("A" -> 1, "B" -> 5))
  }

  it can "be divided by another tally" in {
    val left = Tally("A" -> 2, "B" -> 8)
    val right = Tally("A" -> 1, "B" -> 2)

    assert(left / right === Tally("A" -> 2, "B" -> 4))
  }

  it should "count missing keys in the numerator as 0 when dividing" in {
    val left = Tally("A" -> 2d)
    val right = Tally("A" -> 1, "B" -> 2)

    assert(left / right === Tally("A" -> 2, "B" -> 0))
  }

  it should "fail if keys are missing in the denominator" in {
    val left = Tally("A" -> 2, "B" -> 8)
    val right = Tally("A" -> 1d)

    intercept[NoSuchElementException](left / right)
  }

  it can "be divided by a scalar" in {
    val tally = Tally("A" -> 2, "B" -> 8)

    assert(tally / 2 === Tally("A" -> 1, "B" -> 4))
  }

  it should "fail if divided by 0" in {
    val tally = Tally("A" -> 2, "B" -> 8)

    intercept[ArithmeticException](tally / 0)
  }

  it can "be built by incrementing keys by 1" in {
    val builder = Tally.Builder[String]()

    builder.increment("A")
    builder.increment("A")
    builder.increment("B")

    val tally = builder.build()

    assert(tally === Tally("A" -> 2, "B" -> 1))
  }

  it can "be built by incrementing keys by any amount" in {
    val builder = Tally.Builder[String]()

    builder.incrementBy("A", 2.5)
    builder.incrementBy("B", 1)

    val tally = builder.build()

    assert(tally === Tally("A" -> 2.5, "B" -> 1))
  }

  it can "be built directly from a map" in {
    val map: Map[String, Double] = Map("A" -> 1d)

    assert(Tally(map) === map.toTally)
  }

  it should "support result lookup directly" in {
    val tally = Tally("A" -> 2, "B" -> 8)

    assert(tally("A") === 2)
  }
}
