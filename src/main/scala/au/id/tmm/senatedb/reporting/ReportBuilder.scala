package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.reportwriting.Report
import au.id.tmm.senatedb.tallies.{Tallier, Tallies}

trait ReportBuilder {

  def tableBuilders: Vector[TableBuilder]

  final def requiredTalliers: Set[Tallier] = tableBuilders.flatMap(_.requiredTalliers).toSet

  def reportTitle: String

  final def buildReportFrom(tallies: Tallies): Report = {
    val tables = tableBuilders.map(_.titledTableFrom(tallies))

    Report(reportTitle, tables)
  }
}
