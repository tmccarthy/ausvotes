package au.id.tmm.senatedb.core.reporting

import au.id.tmm.senatedb.core.reporting.PerBallotTallierReportBuilder._

trait StandardReportBuilder extends PerBallotTallierReportBuilder
  with IncludesNationalTally
  with IncludesPerFirstPreferenceTally
  with IncludesPerStateTally
  with IncludesPerDivisionTally
  with IncludesPerFirstPreferencedGroupTally {

  def primaryCountColumnTitle: String

  override def reportTitle: String

  override def tableBuilders: Vector[TableBuilder] = {
    Vector(
      nationalTallyTableBuilder,
      perFirstPreferenceTableBuilder,
      perStateTableBuilder
    ) ++ perGroupTableBuilders ++
    Vector(
      perDivisionTableBuilder
    )
  }
}
