package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.model.{Division, State}
import au.id.tmm.senatedb.reporting.ReportTable._

object ReportTableConstruction {

  def constructPerStateTable(statesInOrder: Vector[State],
                             talliesColTitle: String,
                             fractionColTitle: String = "%",
                             totalRowTitle: String = "Total",
                             talliesPerState: State => Int,
                             denominatorType: DenominatorType,
                             totalFormalBallots: Int,
                             totalFormalBallotsPerState: Map[State, Int]
                            ): ReportTable = {
    val denominatorsPerState = denominatorsForState(totalFormalBallots, totalFormalBallotsPerState, denominatorType)

    val rows = statesInOrder
      .map { state =>
        val tally = talliesPerState(state)
        val denominator = denominatorsPerState(state)

        val fraction = fractionOf(tally, denominator)

        StateRow(state, tally, fraction)
      }

    val totalsRow = {
      val totalRowTally = rows.map(_.tally).sum
      val totalRowFraction = totalRowTally.toDouble / totalFormalBallots.toDouble

      TotalRow(totalRowTitle, totalRowTally, Some(totalRowFraction))
    }

    val columns = Vector(StateNameColumn, TallyColumn(talliesColTitle))

    val table = ReportTable(
      rows :+ totalsRow,
      columns
    )

    ReportTable.addFractionColumnIfDefinedForAllRows(table, fractionColTitle)
  }

  def constructPerDivisionTable(divisionsInOrder: Vector[Division],
                                talliesColTitle: String,
                                fractionColTitle: String = "%",
                                totalRowTitle: String = "Total",
                                talliesPerDivision: Division => Int,
                                denominatorType: DenominatorType,
                                totalFormalBallots: Int,
                                totalFormalBallotsPerState: Map[State, Int],
                                totalFormalBallotsPerDivision: Map[Division, Int]
                               ): ReportTable = {
    val denominatorsPerDivision = denominatorsForDivision(totalFormalBallots,
      totalFormalBallotsPerState,
      totalFormalBallotsPerDivision, denominatorType)

    val rows = divisionsInOrder
      .map { division =>
        val tally = talliesPerDivision(division)
        val denominator = denominatorsPerDivision(division)

        val fraction = fractionOf(tally, denominator)

        DivisionRow(division, tally, fraction)
      }

    val totalsRow = {
      val totalRowTally = rows.map(_.tally).sum
      val totalRowFraction = totalRowTally.toDouble / totalFormalBallots.toDouble

      TotalRow(totalRowTitle, totalRowTally, Some(totalRowFraction))
    }

    val columns = Vector(DivisionNameColumn, StateNameColumn, TallyColumn(talliesColTitle))

    val table = ReportTable(
      rows :+ totalsRow,
      columns
    )

    ReportTable.addFractionColumnIfDefinedForAllRows(table, fractionColTitle)
  }

  private def denominatorsForDivision(totalFormalBallots: Int,
                                      totalFormalBallotsPerState: Map[State, Int],
                                      totalFormalBallotsPerDivision: Map[Division, Int],
                                      denominatorType: DenominatorType): Division => Option[Int] = {
    denominatorType match {
      case DenominatorType.ByTotal => _ => Some(totalFormalBallots)
      case DenominatorType.ByStateTotal => d => totalFormalBallotsPerState.get(d.state)
      case DenominatorType.ByDivisionTotal => d => totalFormalBallotsPerDivision.get(d)
      case DenominatorType.None => _ => None
    }
  }

  private def denominatorsForState(totalFormalBallots: Int,
                                   totalFormalBallotsPerState: Map[State, Int],
                                   denominatorType: DenominatorType): State => Option[Int] = {
    denominatorType match {
      case DenominatorType.ByTotal => _ => Some(totalFormalBallots)
      case DenominatorType.ByStateTotal => s => totalFormalBallotsPerState.get(s)
      case DenominatorType.ByDivisionTotal => throw new IllegalArgumentException("Cant divide a state tally by the division total")
      case DenominatorType.None => _ => None
    }
  }

  private def fractionOf(numerator: Int, denominator: Option[Int]) = denominator
    .map(numerator.toDouble / _.toDouble)

}
