package au.id.tmm.ausvotes.core.reporting

import au.id.tmm.ausvotes.core.reportwriting.Report
import au.id.tmm.ausvotes.core.tallies.{Tallier, TallyBundle}

trait ReportBuilder {

  def tableBuilders: Vector[TableBuilder]

  final def requiredTalliers: Set[Tallier] = tableBuilders.flatMap(_.requiredTalliers).toSet

  def reportTitle: String

  final def buildReportFrom(tallies: TallyBundle): Report = {
    val tables = tableBuilders.map(_.titledTableFrom(tallies))

    Report(reportTitle, tables)
  }
}
