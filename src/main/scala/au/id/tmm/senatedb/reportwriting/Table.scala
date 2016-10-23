package au.id.tmm.senatedb.reportwriting

// TODO could go into utils project
trait Table[R, C] {

  def rows: Vector[R]

  def columns: Vector[C]

  def columnHeading(column: C): String

  def valueAt(row: R, column: C): String

  def isLastColumnBold: Boolean

  def asMarkdown: String = (Vector(headingsRow, barRow) ++ bodyRows).mkString("\n")

  protected def headingsRow: String = columns.toStream
    .map(columnHeading)
    .mkString("|")

  protected def barRow: String = Vector.fill(columns.size)("---").mkString("|")

  protected def bodyRows: Vector[String] = rows.init.map(bodyRowOf(_)) :+ bodyRowOf(rows.last, bold = isLastColumnBold)

  protected def bodyRowOf(row: R, bold: Boolean = false): String = columns
    .map(valueAt(row, _))
    .map(s => if (bold) s"**$s**" else s)
    .map(_.replace("|", "&#124;"))
    .mkString("|")
}
