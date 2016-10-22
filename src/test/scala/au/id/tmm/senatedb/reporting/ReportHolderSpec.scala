package au.id.tmm.senatedb.reporting

import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ReportHolderSpec extends ImprovedFlatSpec {

  "a report holder" should "accumulate correctly" in {
    val left = ReportHolder(
      totalFormal = tallyReportWithTotal(5),
      oneAtl = tallyReportWithTotal(4),
      donkeyVotes = tallyReportWithTotal(3),
      ballotsUsingTicks = tallyReportWithTotal(2),
      ballotsUsingCrosses = tallyReportWithTotal(1)
    )

    val right = ReportHolder(
      totalFormal = tallyReportWithTotal(1),
      oneAtl = tallyReportWithTotal(2),
      donkeyVotes = tallyReportWithTotal(3),
      ballotsUsingTicks = tallyReportWithTotal(4),
      ballotsUsingCrosses = tallyReportWithTotal(5)
    )

    val expected = ReportHolder(
      left.totalFormal + right.totalFormal,
      left.oneAtl + right.oneAtl,
      left.donkeyVotes + right.donkeyVotes,
      left.ballotsUsingTicks + right.ballotsUsingTicks,
      left.ballotsUsingCrosses + right.ballotsUsingCrosses
    )

    assert((left accumulate right) === expected)
  }

  private def tallyReportWithTotal(total: Long) = TallyReport(7, Map.empty, Map.empty, Map.empty, Map.empty)

}
