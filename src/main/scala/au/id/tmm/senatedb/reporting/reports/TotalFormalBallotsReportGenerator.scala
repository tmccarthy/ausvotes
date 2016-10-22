package au.id.tmm.senatedb.reporting.reports

import au.id.tmm.senatedb.computations.BallotWithFacts
import au.id.tmm.senatedb.reporting.{ReportGenerator, TallyReport, TallyReportGenerator}

object TotalFormalBallotsReportGenerator extends ReportGenerator with TallyReportGenerator {
  override type T_REPORT = TallyReport

  override protected def shouldCount(ballot: BallotWithFacts): Boolean = true
}
