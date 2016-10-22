package au.id.tmm.senatedb.reporting

import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ReportAccumulationUtilsSpec extends ImprovedFlatSpec {

  "tally map combination" should "sum the values" in {
    val left = Map("A" -> 1l, "B" -> 2l)
    val right = Map("A" -> 0l, "B" -> 3l)

    val expected = Map("A" -> 1l, "B" -> 5l)

    assert(ReportAccumulationUtils.combineTallies(left, right) === expected)
  }

  it should "default missing values to 0" in {
    val left = Map("A" -> 1l, "B" -> 2l)
    val right = Map("B" -> 3l)

    val expected = Map("A" -> 1l, "B" -> 5l)

    assert(ReportAccumulationUtils.combineTallies(left, right) === expected)
  }

}
