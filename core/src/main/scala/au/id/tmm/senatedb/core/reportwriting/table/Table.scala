package au.id.tmm.senatedb.core.reportwriting.table

import au.id.tmm.senatedb.core.reportwriting.table.Table.StringIterableOps
import au.id.tmm.utilities.collection.Matrix

// TODO could go into utils project
trait Table[R, C] {

  def rows: Vector[R]

  def columns: Vector[C]

  def columnHeading(column: C): String

  def valueAt(row: R, column: C): String

  def isLastColumnBold: Boolean

  def asMarkdown: String = (Vector(headingsRow, barRow) ++ bodyRows).mkString("\n")

  protected def headingsRow: String = columns
    .map(columnHeading)
    .mkMarkdownRow

  protected def barRow: String = Vector.fill(columns.size)("---").mkMarkdownRow

  protected def bodyRows: Vector[String] = rows.init.map(bodyRowOf(_)) :+ bodyRowOf(rows.last, bold = isLastColumnBold)

  protected def bodyRowOf(row: R, bold: Boolean = false): String = columns
    .map(valueAt(row, _))
    .map(s => if (bold) s"**$s**" else s)
    .map(_.replace("|", "&#124;"))
    .mkMarkdownRow

  def asMatrix: Matrix[String] = {
    val headingsRow = columns.map(columnHeading)

    val data = rows.map(row => columns.map(column => valueAt(row, column)))

    Matrix(Vector(headingsRow) ++ data)
  }
}

object Table {
  implicit class StringIterableOps(strings: Vector[String]) {
    def mkMarkdownRow = strings.toStream
      .map(_.trim)
      .map(string => if (string.isEmpty) " " else string)
      .mkString("|", "|", "|")
  }
}