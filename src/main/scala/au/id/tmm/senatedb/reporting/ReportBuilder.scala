package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.reportwriting.Report
import au.id.tmm.senatedb.tallies.{Tallier, Tallies}

trait ReportBuilder {

  def requiredTalliers: Set[Tallier]

  def buildTableFrom(tallies: Tallies): Report

}
