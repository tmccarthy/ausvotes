package au.id.tmm.ausvotes.core.reportwriting.table

import au.id.tmm.ausvotes.core.model.computation.SavingsProvision
import au.id.tmm.ausvotes.core.reportwriting.table.Row.DataRow
import au.id.tmm.ausvotes.model.federal.FederalVcpJurisdiction
import au.id.tmm.ausvotes.model.federal.senate.SenateElectionForState
import au.id.tmm.ausvotes.model.stv.{Group, Ungrouped}
import au.id.tmm.ausvotes.model.{Electorate, Party, PartySignificance, VoteCollectionPoint}
import au.id.tmm.utilities.geo.australia.State

import scala.annotation.tailrec

sealed trait Column {
  def heading: String

  def valueFor(dataRow: DataRow[Any]): String
}

object Column {

  trait KeyBasedColumn extends Column {
    final def valueFor(dataRow: DataRow[Any]): String = valueForKey(dataRow.key)

    protected def valueForKey(key: Any): String
  }

  case object EmptyColumn extends KeyBasedColumn {
    override def heading: String = ""

    override protected def valueForKey(key: Any): String = ""
  }

  case object StateNameColumn extends KeyBasedColumn {
    val heading = "State"

    @tailrec
    override def valueForKey(key: Any): String = key match {
      case s: State => s.abbreviation
      case Group(SenateElectionForState(_, state), _, _) => valueForKey(state)
      case Ungrouped(SenateElectionForState(_, state)) => valueForKey(state)
      case Electorate(_, state: State, _, _) => valueForKey(state)
      case VoteCollectionPoint.PollingPlace(_, FederalVcpJurisdiction(state, _), _, _, _, _) => valueForKey(state)
      case VoteCollectionPoint.Special(_, FederalVcpJurisdiction(state, _), _, _) => valueForKey(state)
    }
  }

  case object DivisionNameColumn extends KeyBasedColumn {
    val heading = "Division"

    @tailrec
    override def valueForKey(key: Any): String = key match {
      case Electorate(_, _, name, _) => name
      case VoteCollectionPoint.PollingPlace(_, FederalVcpJurisdiction(state, _), _, _, _, _) => valueForKey(state)
      case VoteCollectionPoint.Special(_, FederalVcpJurisdiction(state, _), _, _) => valueForKey(state)
    }
  }

  case object VoteCollectionPointNameColumn extends KeyBasedColumn {
    val heading = "Vote collection point"

    override def valueForKey(key: Any): String = key match {
      case v: VoteCollectionPoint[_, _] => v.name
    }
  }

  case object PartyNameColumn extends KeyBasedColumn {
    val heading = "Party"

    @tailrec
    override def valueForKey(key: Any): String = key match {
      case Some(Party(partyName)) => partyName
      case None => "Independent"
      case Group(_, _, party) => valueForKey(party)
      case Ungrouped(_) => valueForKey(None)
    }
  }

  case object PartyTypeColumn extends KeyBasedColumn {
    val heading = "Party type"

    override def valueForKey(key: Any): String = key match {
      case PartySignificance.Major => "Major parties"
      case PartySignificance.Minor => "Minor parties"
      case PartySignificance.Independent => "Independents"
    }
  }

  case object GroupNameColumn extends KeyBasedColumn {
    val heading = "Group"

    override def valueForKey(key: Any): String = key match {
      case Group(_, code, party) => s"${code.asString} (${PartyNameColumn.valueForKey(party)})"
      case Ungrouped(_) => s"${Ungrouped.code} (Ungrouped)"
    }
  }

  case object SavingsProvisionNameColumn extends KeyBasedColumn {
    val heading = "Savings provision"

    override protected def valueForKey(key: Any): String = key match {
      case SavingsProvision.UsedTick => "Used tick for first preference"
      case SavingsProvision.UsedCross => "Used cross for first preference"
      case SavingsProvision.CountingErrorAtl => "Counting error above-the-line"
      case SavingsProvision.CountingErrorBtl => "Counting error below-the-line"
      case SavingsProvision.InsufficientPreferencesAtl => "Insufficient squares numbered above-the-line"
      case SavingsProvision.InsufficientPreferencesBtl => "Insufficient squares numbered below-the-line"
    }
  }

  final case class PrimaryCountColumn(heading: String) extends Column {

    override def valueFor(dataRow: DataRow[Any]): String = valueForCount(dataRow.count)

    def valueForCount(count: Double): String = {
      TallyTable.tallyFormat.format(count)
    }
  }

  final case class DenominatorCountColumn(heading: String) extends Column {

    override def valueFor(dataRow: DataRow[Any]): String = valueForDenominator(dataRow.denominator)

    def valueForDenominator(denominator: Double): String = TallyTable.tallyFormat.format(denominator)
  }

  final case class FractionColumn(heading: String = "%") extends Column {

    override def valueFor(dataRow: DataRow[Any]): String = {
      val fraction = dataRow.fraction
      valueForFraction(fraction)
    }

    def valueForFraction(fraction: Double): String = {
      if (fraction.isNaN) {
        "N/A"
      } else {
        TallyTable.fractionFormat.format(fraction)
      }
    }
  }

}
