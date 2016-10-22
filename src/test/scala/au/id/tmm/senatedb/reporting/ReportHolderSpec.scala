package au.id.tmm.senatedb.reporting

import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ReportHolderSpec extends ImprovedFlatSpec {

  "a report holder" should "accumulate correctly" in {
    val leftTotalReport = TallyReport(5, Map.empty, Map.empty, Map.empty, Map.empty)
    val left = ReportHolder(leftTotalReport)

    val rightTotalReport = TallyReport(6, Map.empty, Map.empty, Map.empty, Map.empty)
    val right = ReportHolder(rightTotalReport)

    assert((left accumulate right) === ReportHolder(leftTotalReport accumulate rightTotalReport))
  }

}
