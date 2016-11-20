package au.id.tmm.senatedb.core.reporting

import au.id.tmm.senatedb.core.model.parsing.{BallotGroup, Division, Party}
import au.id.tmm.senatedb.core.tallies.PerBallotTallier
import au.id.tmm.senatedb.core.tallies.Tallier.{NormalTallier, SimpleTallier, TieredTallier}
import au.id.tmm.utilities.geo.australia.State

trait StandardReportBuilder extends ReportBuilder {

  def primaryCountColumnTitle: String

  override def reportTitle: String

  def perBallotTallier: PerBallotTallier

  final def nationalTallier: SimpleTallier = perBallotTallier.Nationally

  final def nationalPerFirstPreferenceTallier: NormalTallier[Party] = perBallotTallier.NationallyByFirstPreference

  final def perStateTallier: NormalTallier[State] = perBallotTallier.ByState

  final def perDivisionTallier: NormalTallier[Division] = perBallotTallier.ByDivision

  final def perFirstPreferencedGroupTallier: TieredTallier[State, BallotGroup] = perBallotTallier.ByFirstPreferencedGroup

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
