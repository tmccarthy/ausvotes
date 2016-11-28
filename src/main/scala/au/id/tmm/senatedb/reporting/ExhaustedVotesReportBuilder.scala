package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.reporting.PerBallotTallierReportBuilder.IncludesTableByPartyType
import au.id.tmm.senatedb.reporting.TableBuilders.NationalPerFirstPrefTableBuilder
import au.id.tmm.senatedb.tallies.{CountExhaustedVotes, PerBallotTallier}

object ExhaustedVotesReportBuilder extends StandardReportBuilder with IncludesTableByPartyType {
  override def primaryCountColumnTitle: String = "Exhausted votes"

  override def reportTitle: String = "Exhausted votes"

  override def perBallotTallier: PerBallotTallier = CountExhaustedVotes

  override def tableBuilders: Vector[TableBuilder] = {
    val normalTableBuilders = super.tableBuilders

    val indexToInsertAfter = normalTableBuilders.indexWhere(_.isInstanceOf[NationalPerFirstPrefTableBuilder])

    val (leftOfPerPartyTypeTable, rightOfPerPartyTypeTable) = normalTableBuilders.splitAt(indexToInsertAfter + 1)

    (leftOfPerPartyTypeTable :+ perPartyTypeTableBuilder) ++ rightOfPerPartyTypeTable
  }
}
