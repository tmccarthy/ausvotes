package au.id.tmm.senatedb.core.reporting

import au.id.tmm.senatedb.core.reporting.TableBuilders.NationalPerFirstPrefTableBuilder

trait IncludesTableByPartyType extends StandardReportBuilder {
  override def tableBuilders: Vector[TableBuilder] = {
    val normalTableBuilders = super.tableBuilders

    val perPartyTypeTableBuilder = TableBuilders.NationallyPerPartyTypeTableBuilder(
      perBallotTallier.Nationally,
      perBallotTallier.NationallyByFirstPreference,
      primaryCountColumnTitle
    )

    val indexToInsertAfter = normalTableBuilders.indexWhere(_.isInstanceOf[NationalPerFirstPrefTableBuilder])

    val (leftOfPerPartyTypeTable, rightOfPerPartyTypeTable) = normalTableBuilders.splitAt(indexToInsertAfter + 1)

    (leftOfPerPartyTypeTable :+ perPartyTypeTableBuilder) ++ rightOfPerPartyTypeTable
  }
}
