package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.reporting.PerBallotTallierReportBuilder.IncludesTableByPartyType
import au.id.tmm.senatedb.reporting.TableBuilders.NationalPerFirstPrefTableBuilder
import au.id.tmm.senatedb.tallies.{CountExhaustedBallots, PerBallotTallier}

object ExhaustedBallotsReportBuilder extends StandardReportBuilder with IncludesTableByPartyType {
  override def primaryCountColumnTitle: String = "Exhausted ballots"

  override def reportTitle: String = "Exhausted ballots"

  override def perBallotTallier: PerBallotTallier = CountExhaustedBallots

  override def tableBuilders: Vector[TableBuilder] = {
    val normalTableBuilders = super.tableBuilders

    val indexToInsertAfter = normalTableBuilders.indexWhere(_.isInstanceOf[NationalPerFirstPrefTableBuilder])

    val (leftOfPerPartyTypeTable, rightOfPerPartyTypeTable) = normalTableBuilders.splitAt(indexToInsertAfter + 1)

    (leftOfPerPartyTypeTable :+ perPartyTypeTableBuilder) ++ rightOfPerPartyTypeTable
  }
}
