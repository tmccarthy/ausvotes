package au.id.tmm.senatedb.reporting

import java.text.DecimalFormat

import au.id.tmm.senatedb.model.{Division, State}
import au.id.tmm.senatedb.reporting.ReportTable.{Column, Row, fractionFormat, tallyFormat}

import scala.annotation.tailrec

final case class ReportTable(rows: Vector[Row], columns: Vector[Column]) extends Table[Row, Column] {
  override def columnHeading(column: Column): String = column.heading

  override def valueAt(row: Row, column: Column): String = convertToString(row.valueFor(column))

  @tailrec
  private def convertToString(value: Any): String = value match {
    case None => ""
    case Some(v) => convertToString(v)
    case i: Int => tallyFormat.format(i)
    case d: Double => fractionFormat.format(d)
    case s: String => s
    case _ => throw new UnsupportedOperationException(s"Can't render value of type ${value.getClass.getCanonicalName}")
  }

  override def isLastColumnBold: Boolean = true
}

object ReportTable {
  val fractionFormat = new DecimalFormat("#0.00%")
  val tallyFormat = new DecimalFormat("#,###")

  def addFractionColumnIfDefinedForAllRows(table: ReportTable, fractionColTitle: String = "%"): ReportTable = {
    def colIsFractionColumn(c: Column) = c match {
      case FractionColumn(_) => true
      case _ => false
    }

    val alreadyHasFractionCol = table.columns.exists(colIsFractionColumn)

    if (alreadyHasFractionCol) {
      return table
    }

    def rowHasFraction(r: Row) = r match {
      case StateRow(_, _, f) => f.isDefined
      case DivisionRow(_, _, f) => f.isDefined
      case TotalRow(_, _, f) => f.isDefined
      case _ => false
    }

    val fractionDefinedForAllRows = table.rows.forall(rowHasFraction)

    if (fractionDefinedForAllRows) {
      val newColumns: Vector[Column] = table.columns :+ FractionColumn(fractionColTitle)

      ReportTable(table.rows, newColumns)
    } else {
      table
    }
  }

  sealed trait Row {
    def valueFor(column: Column): Any
  }

  final case class StateRow(state: State, tally: Int, fraction: Option[Double]) extends Row {
    override def valueFor(column: Column): Any = column match {
      case StateNameColumn => state.shortName
      case DivisionNameColumn => throw new IllegalArgumentException("Can't have a division for a state row")
      case TallyColumn(_) => tally
      case FractionColumn(_) => fraction
    }
  }

  final case class DivisionRow(division: Division, tally: Int, fraction: Option[Double]) extends Row {
    override def valueFor(column: Column): Any = column match {
      case StateNameColumn => division.state.shortName
      case DivisionNameColumn => division.name
      case TallyColumn(_) => tally
      case FractionColumn(_) => fraction
    }
  }

  final case class TotalRow(title: String = "Total", tally: Int, fraction: Option[Double]) extends Row {
    override def valueFor(column: Column): Any = column match {
      case StateNameColumn => title
      case DivisionNameColumn => title
      case TallyColumn(_) => tally
      case FractionColumn(_) => fraction
    }
  }

  sealed trait Column {
    def heading: String
  }
  case object StateNameColumn extends Column {
    val heading = "State"
  }
  case object DivisionNameColumn extends Column {
    val heading = "Division"
  }
  final case class TallyColumn(heading: String) extends Column
  final case class FractionColumn(heading: String = "%") extends Column
}