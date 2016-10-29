package au.id.tmm.senatedb.reportwriting

import java.text.DecimalFormat

import au.id.tmm.senatedb.model.parsing._
import au.id.tmm.senatedb.reportwriting.TallyTable._
import au.id.tmm.senatedb.tallies.Tally
import au.id.tmm.utilities.geo.australia.State

import scala.annotation.tailrec

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
      case c: PrimaryCountColumn => tallyFormat.format(primaryCountTotal)
      case c: DenominatorCountColumn => tallyFormat.format(denominatorCountTotal)
      case c: FractionColumn => fractionFormat.format(primaryCountTotal / denominatorCountTotal)
      case c if columns.indexOf(c) == 0 => "Total"
      case _ => ""
    }
  }

  override def isLastColumnBold: Boolean = true
}

object TallyTable {
  val fractionFormat = new DecimalFormat("#0.00%")
  val tallyFormat = new DecimalFormat("#,###")

  def defaultOrdering[A] = new Ordering[DataRow[A]] {
    override def compare(left: DataRow[A], right: DataRow[A]): Int = left.count compare right.count
  }.reverse

  sealed trait Row[+A]

  final case class DataRow[+A](key: A, count: Double, denominator: Double, fraction: Double) extends Row[A]
  case object TotalsRow extends Row[Nothing]

  sealed trait Column {
    def heading: String

    def valueFor(dataRow: DataRow[Any]): String
  }

  trait KeyBasedColumn extends Column {
    final def valueFor(dataRow: DataRow[Any]): String = valueForKey(dataRow.key)

    protected def valueForKey(key: Any): String
  }

  case object StateNameColumn extends KeyBasedColumn {
    val heading = "State"

    @tailrec
    override def valueForKey(key: Any): String = key match {
      case s: State => s.abbreviation
      case Group(_, state, _, _) => valueForKey(state)
      case Ungrouped(state) => valueForKey(state)
      case Division(_, state, _, _) => valueForKey(state)
      case v: VoteCollectionPoint => valueForKey(v.state)
    }
  }

  case object DivisionNameColumn extends KeyBasedColumn {
    val heading = "Division"

    @tailrec
    override def valueForKey(key: Any): String = key match {
      case Division(_, _, name, _) => name
      case v: VoteCollectionPoint => valueForKey(v.division)
    }
  }

  case object VoteCollectionPointNameColumn extends KeyBasedColumn {
    val heading = "Vote collection point"

    override def valueForKey(key: Any): String = key match {
      case v: VoteCollectionPoint => v.name
    }
  }

  case object PartyNameColumn extends KeyBasedColumn {
    val heading = "Party"

    @tailrec
    override def valueForKey(key: Any): String = key match {
      case RegisteredParty(name) => name
      case Independent => "Independent"
      case Group(_, _, _, party) => valueForKey(party)
      case Ungrouped(_) => valueForKey(Independent)
    }
  }

  case object GroupColumn extends KeyBasedColumn {
    val heading = "Group"

    override def valueForKey(key: Any): String = key match {
      case Group(_, _, code, party) => s"$code (${PartyNameColumn.valueForKey(party)})"
      case Ungrouped(_) => s"${Ungrouped.code} (Ungrouped)"
    }
  }

  final case class PrimaryCountColumn(heading: String) extends Column {

    override def valueFor(dataRow: DataRow[Any]): String = tallyFormat.format(dataRow.count)
  }

  final case class DenominatorCountColumn(heading: String) extends Column {

    override def valueFor(dataRow: DataRow[Any]): String = tallyFormat.format(dataRow.denominator)
  }

  final case class FractionColumn(heading: String = "%") extends Column {

    override def valueFor(dataRow: DataRow[Any]): String = fractionFormat.format(dataRow.fraction)
  }
}