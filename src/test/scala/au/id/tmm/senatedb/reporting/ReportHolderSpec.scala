package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.reporting.totalformal.TotalFormalBallotsReport
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ReportHolderSpec extends ImprovedFlatSpec {

  "a report holder" should "accumulate correctly" in {
    val left = ReportHolder(TotalFormalBallotsReport(5))
    val right = ReportHolder(TotalFormalBallotsReport(6))

    assert((left accumulate right) === ReportHolder(TotalFormalBallotsReport(11)))
  }

}
