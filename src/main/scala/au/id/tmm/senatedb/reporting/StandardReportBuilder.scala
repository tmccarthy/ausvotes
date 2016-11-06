package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.model.parsing.{BallotGroup, Division, Party}
import au.id.tmm.senatedb.tallies.PerBallotTallier
import au.id.tmm.senatedb.tallies.Tallier.{NormalTallier, SimpleTallier, TieredTallier}
import au.id.tmm.utilities.geo.australia.State

trait StandardReportBuilder extends ReportBuilder {

  def primaryCountColumnTitle: String

  override def reportTitle: String

  def predicateTallier: PerBallotTallier

  final def nationalTallier: SimpleTallier = predicateTallier.Nationally

  final def nationalPerFirstPreferenceTallier: NormalTallier[Party] = predicateTallier.NationallyByFirstPreference

  final def perStateTallier: NormalTallier[State] = predicateTallier.ByState

  final def perDivisionTallier: NormalTallier[Division] = predicateTallier.ByDivision

  final def perFirstPreferencedGroupTallier: TieredTallier[State, BallotGroup] = predicateTallier.ByFirstPreferencedGroup

  override def tableBuilders: Vector[TableBuilder] = {
    val perGroupTableBuilders = State.ALL_STATES
      .toVector
      .sorted
      .map(state => TableBuilders.PerGroupTableBuilder(perStateTallier, perFirstPreferencedGroupTallier,
        primaryCountColumnTitle, state)
      )

    Vector(
      TableBuilders.NationalTallyTableBuilder(nationalTallier, primaryCountColumnTitle),
      TableBuilders.NationalPerFirstPrefTableBuilder(nationalTallier, nationalPerFirstPreferenceTallier, primaryCountColumnTitle),
      TableBuilders.PerStateTableBuilder(nationalTallier, perStateTallier, primaryCountColumnTitle)
    ) ++ perGroupTableBuilders ++
    Vector(
      TableBuilders.PerDivisionTableBuilder(nationalTallier, perDivisionTallier, primaryCountColumnTitle)
    )
  }
}
