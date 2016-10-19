package au.id.tmm.senatedb.reporting.totalformal

import au.id.tmm.utilities.testing.ImprovedFlatSpec

class TotalFormalBallotsReportSpec extends ImprovedFlatSpec {

  "a TotalFormalBallotsReport" should "accumulate by adding the respective totals" in {
    val left = TotalFormalBallotsReport(5)
    val right = TotalFormalBallotsReport(6)

    assert((left accumulate right) === TotalFormalBallotsReport(11))
  }

}
