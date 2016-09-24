package au.id.tmm.senatedb.reporting

final case class Report(title: String,
                        perState: ReportTable,
                        perDivision: ReportTable,
                        description: String = "")
