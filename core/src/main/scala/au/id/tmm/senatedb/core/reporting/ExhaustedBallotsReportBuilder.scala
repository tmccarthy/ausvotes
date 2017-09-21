package au.id.tmm.senatedb.core.reporting

import au.id.tmm.senatedb.core.reporting.PerBallotTallierReportBuilder.IncludesTableByPartyType
import au.id.tmm.senatedb.core.reporting.TableBuilders.NationalPerFirstPrefTableBuilder
import au.id.tmm.senatedb.core.tallies.BallotCounter

object ExhaustedBallotsReportBuilder extends StandardReportBuilder with IncludesTableByPartyType {
  override def primaryCountColumnTitle: String = "Exhausted ballots"

  override def reportTitle: String = "Exhausted ballots"

  override def ballotCounter = BallotCounter.ExhaustedBallots

  override def tableBuilders: Vector[TableBuilder] = {
    val normalTableBuilders = super.tableBuilders

    val indexToInsertAfter = normalTableBuilders.indexWhere(_.isInstanceOf[NationalPerFirstPrefTableBuilder])

    val (leftOfPerPartyTypeTable, rightOfPerPartyTypeTable) = normalTableBuilders.splitAt(indexToInsertAfter + 1)

    (leftOfPerPartyTypeTable :+ perPartyTypeTableBuilder) ++ rightOfPerPartyTypeTable
  }
}
