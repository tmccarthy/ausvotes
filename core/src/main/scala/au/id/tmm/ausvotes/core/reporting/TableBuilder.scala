package au.id.tmm.ausvotes.core.reporting

import au.id.tmm.ausvotes.core.reportwriting.Report.TitledTable
import au.id.tmm.ausvotes.core.reportwriting.table.TallyTable
import au.id.tmm.ausvotes.core.tallies.{Tallier, TallyBundle}

trait TableBuilder {

  def requiredTalliers: Set[Tallier]

  def tableFrom(tallies: TallyBundle): TallyTable[_]

  def tableTitle: String

  def titledTableFrom(tallies: TallyBundle): TitledTable = TitledTable(tableTitle, tableFrom(tallies))

}
