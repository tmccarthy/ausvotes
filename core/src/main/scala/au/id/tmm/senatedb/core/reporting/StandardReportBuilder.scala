package au.id.tmm.senatedb.core.reporting

import au.id.tmm.senatedb.core.model.parsing.{BallotGroup, Division, Party}
import au.id.tmm.senatedb.core.tallies.PerBallotTallier
import au.id.tmm.senatedb.core.tallies.Tallier.{NormalTallier, SimpleTallier, TieredTallier}
import au.id.tmm.senatedb.core.reporting.PerBallotTallierReportBuilder._
import au.id.tmm.utilities.geo.australia.State

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
