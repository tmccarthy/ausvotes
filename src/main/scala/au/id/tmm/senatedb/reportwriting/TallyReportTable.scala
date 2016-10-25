package au.id.tmm.senatedb.reportwriting
import java.text.DecimalFormat

import au.id.tmm.senatedb.model.parsing._
import au.id.tmm.senatedb.reportwriting.TallyReportTable._
import au.id.tmm.utilities.geo.australia.State

import scala.annotation.tailrec

final case class TallyReportTable[A](tallies: Map[A, Long],
                                     fractions: Map[A, Double],
                                     total: Long,
                                     totalFraction: Option[Double],
                                     columns: Vector[Column],
                                     rowOrdering: Ordering[(A, Long)] = TallyReportTable.defaultOrdering[A]
                                    ) extends Table[TallyReportTable.Row[A], TallyReportTable.Column]{
  override def rows: Vector[TallyReportTable.Row[A]] = {
    val valueRows = tallies.toStream
      .sorted(rowOrdering)
      .map { case (element, tally) => ValueRow(element) }

    valueRows.toVector ++ Vector(TotalsRow)
  }

  override def columnHeading(column: Column): String = column.heading

  override def valueAt(row: Row[A], column: Column): String = row match {
    case TotalsRow => {
      column match {
        case TallyColumn(_) => tallyFormat.format(total)
        case FractionColumn(_) => totalFraction.map(fractionFormat.format).getOrElse("")
        case c if columns.indexOf(c) == 0 => "Total"
        case _ => ""
      }
    }
    case ValueRow(value) => {
      column match {
        case StateNameColumn => stateNameFrom(value)
        case DivisionNameColumn => divisionNameFrom(value)
        case VoteCollectionPointColumn => voteCollectionPointFrom(value)
        case PartyNameColumn => partyNameFrom(value)
        case GroupColumn => groupNameFrom(value)
        case _: TallyColumn => tallyFrom(value)
        case _: FractionColumn => fractionFrom(value)
      }
    }
  }

  @tailrec
  private def stateNameFrom(value: Any): String = {
    value match {
      case State(_, abbreviation, _) => abbreviation
      case Division(_, state, _, _) => stateNameFrom(state)
      case Group(_, state, _, _) => stateNameFrom(state)
      case v: VoteCollectionPoint => stateNameFrom(v.state)
    }
  }

  @tailrec
  private def divisionNameFrom(value: Any): String = {
    value match {
      case Division(_, _, name, _) => name
      case v: VoteCollectionPoint => divisionNameFrom(v.division)
    }
  }

  private def voteCollectionPointFrom(value: Any): String = {
    value match {
      case v: VoteCollectionPoint => v.name
    }
  }

  @tailrec
  private def partyNameFrom(value: Any): String = {
    value match {
      case Party(_, name) => name
      case Some(party) => partyNameFrom(party)
      case None => "Independent"
    }
  }

  private def groupNameFrom(value: Any): String = {
    value match {
      case Group(_, _, code, party) => s"$code (${partyNameFrom(party)})"
      case Ungrouped => s"${Ungrouped.code} (Ungrouped)"
    }
  }

  private def tallyFrom(value: A): String = tallyFormat.format(tallies(value))

  private def fractionFrom(value: A): String = fractionFormat.format(fractions(value))

  override def isLastColumnBold: Boolean = true
}

object TallyReportTable {

  val fractionFormat = new DecimalFormat("#0.00%")
  val tallyFormat = new DecimalFormat("#,###")

  def defaultOrdering[A] = new Ordering[(A, Long)] {
    override def compare(left: (A, Long), right: (A, Long)): Int = left._2 compareTo right._2
  }.reverse

  sealed trait Row[+A]

  final case class ValueRow[+A](value: A) extends Row[A]
  case object TotalsRow extends Row[Nothing]

  sealed trait Column {
    def heading: String
  }
  case object StateNameColumn extends Column {
    val heading = "State"
  }
  case object DivisionNameColumn extends Column {
    val heading = "Division"
  }
  case object VoteCollectionPointColumn extends Column {
    val heading = "Vote collection point"
  }
  case object PartyNameColumn extends Column {
    val heading = "Party"
  }
  case object GroupColumn extends Column {
    val heading = "Group"
  }
  final case class TallyColumn(heading: String) extends Column
  final case class FractionColumn(heading: String = "%") extends Column

}