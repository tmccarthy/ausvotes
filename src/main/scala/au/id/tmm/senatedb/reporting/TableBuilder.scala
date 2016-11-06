package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.reportwriting.Report.TitledTable
import au.id.tmm.senatedb.reportwriting.table.TallyTable
import au.id.tmm.senatedb.tallies.{Tallier, Tallies}

trait TableBuilder {

  def requiredTalliers: Set[Tallier]

  def tableFrom(tallies: Tallies): TallyTable[_]

  def tableTitle: String

  def titledTableFrom(tallies: Tallies): TitledTable = TitledTable(tableTitle, tableFrom(tallies))

}
