package au.id.tmm.senatedb.reporting

import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.collection.mutable

class ReportAccumulationUtilsSpec extends ImprovedFlatSpec {

  "tally map combination" should "sum the values" in {
    val left = Map("A" -> 1l, "B" -> 2l)
    val right = Map("A" -> 0l, "B" -> 3l)

    val expected = Map("A" -> 1l, "B" -> 5l)

    assert(ReportAccumulationUtils.sumTallies(left, right) === expected)
  }

  it should "default missing values to 0" in {
    val left = Map("A" -> 1l, "B" -> 2l)
    val right = Map("B" -> 3l)

    val expected = Map("A" -> 1l, "B" -> 5l)

    assert(ReportAccumulationUtils.sumTallies(left, right) === expected)
  }

  "tally map division" should "divide by a scalar" in {
    val tally = Map("A" -> 1l, "B" -> 2l)

    val expected = Map("A" -> 0.5d, "B" -> 1d)

    assert(ReportAccumulationUtils.divideTally(tally, 2) === expected)
  }

  it should "divide by another map" in {
    val left = Map("A" -> 1l, "B" -> 2l)
    val right = Map("A" -> 2l, "B" -> 8l)

    val expected = Map("A" -> 0.5d, "B" -> 0.25d)

    assert(ReportAccumulationUtils.divideTally(left, right) === expected)
  }

  it should "throw if the numerator contains a key not in the denominator" in {
    val left = Map("A" -> 1l, "B" -> 2l, "C" -> 4l)
    val right = Map("A" -> 2l, "B" -> 4l)

    intercept[NoSuchElementException](ReportAccumulationUtils.divideTally(left, right))
  }

  "two tiered map combination" should "sum the values" in {
    val left = Map(
      "A" -> Map("a" -> 1l, "b" -> 2l),
      "B" -> Map("b" -> 3l, "c" -> 4l)
    )
    val right = Map(
      "A" -> Map("a" -> 1l, "e" -> 2l),
      "C" -> Map("b" -> 3l, "c" -> 4l)
    )

    val expected = Map(
      "A" -> Map("a" -> 2l, "b" -> 2l, "e" -> 2l),
      "B" -> Map("b" -> 3l, "c" -> 4l),
      "C" -> Map("b" -> 3l, "c" -> 4l)
    )

    assert(ReportAccumulationUtils.sumTieredTallies(left, right) === expected)
  }

  "map incrementing" should "increment the value in the map" in {
    val map = mutable.Map("A" -> 0l, "B" -> 0l)

    ReportAccumulationUtils.increment(map, "B")

    assert(map("B") === 1l)
  }
}
