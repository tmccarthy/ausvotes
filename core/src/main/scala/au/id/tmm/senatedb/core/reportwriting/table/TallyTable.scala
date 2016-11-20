package au.id.tmm.senatedb.core.reportwriting.table

import java.text.DecimalFormat

import au.id.tmm.senatedb.core.reportwriting.Report.TitledTable
import au.id.tmm.senatedb.core.reportwriting.table.Column.{DenominatorCountColumn, FractionColumn, PrimaryCountColumn}
import au.id.tmm.senatedb.core.reportwriting.table.Row.{DataRow, TotalsRow}
import au.id.tmm.senatedb.core.tallies.Tally

final case class TallyTable[A](primaryCount: Tally[A],
                               denominatorLookup: A => Double,
                               primaryCountTotal: Double,
                               denominatorCountTotal: Double,
                               columns: Vector[Column],
                               rowOrdering: Ordering[DataRow[A]] = TallyTable.defaultOrdering[A]
                              )
  extends Table[Row[A], Column] {

  override def rows: Vector[Row[A]] = {
    val dataRows = primaryCount.values.keySet.toStream
      .map(dataRowFor)
      .sorted(rowOrdering)
      .toVector

    dataRows ++ Vector(TotalsRow)
  }

  private def dataRowFor(key: A): DataRow[A] = {
    val count = primaryCount(key)
    val denominator = denominatorLookup(key)

    DataRow(key, count, denominator, count / denominator)
  }

  override def columnHeading(column: Column): String = column.heading

  override def valueAt(row: Row[A], column: Column): String = row match {
    case d: DataRow[A] => column.valueFor(d)
    case TotalsRow => totalsRowValueFor(column)
  }

  private def totalsRowValueFor(column: Column): String = {
    column match {
      case c: PrimaryCountColumn => c.valueForCount(primaryCountTotal)
      case c: DenominatorCountColumn => c.valueForDenominator(denominatorCountTotal)
      case c: FractionColumn => c.valueForFraction(primaryCountTotal / denominatorCountTotal)
      case c if columns.indexOf(c) == 0 => "Total"
      case _ => ""
    }
  }

  override def isLastColumnBold: Boolean = true

  def withTitle(title: String): TitledTable = TitledTable(title, this)
}

object TallyTable {
  def totalRowOnly(primaryCountTotal: Double,
                   denominatorCountTotal: Double,
                   columns: Vector[Column]
                  ): TallyTable[Any] = {
    TallyTable[Any](Tally(), _ => throw new AssertionError(), primaryCountTotal, denominatorCountTotal, columns)
  }

  val fractionFormat = new DecimalFormat("#0.00%")
  val tallyFormat = new DecimalFormat("#,###")

  def defaultOrdering[A] = new Ordering[DataRow[A]] {
    override def compare(left: DataRow[A], right: DataRow[A]): Int = left.count compare right.count
  }.reverse

}