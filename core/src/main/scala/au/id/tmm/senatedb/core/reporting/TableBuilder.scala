package au.id.tmm.senatedb.core.reporting

import au.id.tmm.senatedb.core.reportwriting.Report.TitledTable
import au.id.tmm.senatedb.core.reportwriting.table.TallyTable
import au.id.tmm.senatedb.core.tallies.{Tallier, Tallies}

trait TableBuilder {

  def requiredTalliers: Set[Tallier]

  def tableFrom(tallies: Tallies): TallyTable[_]

  def tableTitle: String

  def titledTableFrom(tallies: Tallies): TitledTable = TitledTable(tableTitle, tableFrom(tallies))

}
