package au.id.tmm.senatedb.core.reportwriting

import au.id.tmm.senatedb.core.reportwriting.Report.TitledTable
import au.id.tmm.senatedb.core.reportwriting.table.TallyTable

final case class Report(title: String,
                        tables: Vector[TitledTable]
                       ) {
  def asMarkdown: String =
    s"# $title\n" +
      s"\n" +
      s"${tables.map(asMarkdown).mkString("\n\n")}\n"

  private def asMarkdown(titledTable: TitledTable): String = {
    s"### ${titledTable.title}\n" +
      s"\n" +
      s"${titledTable.table.asMarkdown}"
  }

}

object Report {
  final case class TitledTable(title: String, table: TallyTable[_])
}