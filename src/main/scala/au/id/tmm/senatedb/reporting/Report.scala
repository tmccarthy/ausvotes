package au.id.tmm.senatedb.reporting

final case class Report(title: String,
                        perState: TableWithSql,
                        perDivision: TableWithSql,
                        description: String = "")
