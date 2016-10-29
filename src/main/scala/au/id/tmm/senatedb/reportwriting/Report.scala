package au.id.tmm.senatedb.reportwriting

import au.id.tmm.senatedb.reportwriting.Report.TitledTable

final case class Report(title: String,
                        tables: Vector[TitledTable]
                       ) {

}

object Report {
  final case class TitledTable(title: String, table: TallyTable[Any])
}