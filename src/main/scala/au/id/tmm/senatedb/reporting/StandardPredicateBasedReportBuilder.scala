package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.model.parsing.{BallotGroup, Division, Party}
import au.id.tmm.senatedb.tallies.Tallier.{NormalTallier, SimpleTallier, TieredTallier}
import au.id.tmm.utilities.geo.australia.State

trait StandardPredicateBasedReportBuilder extends ReportBuilder {

  def primaryCountColumnTitle: String

  def nationalTallier: SimpleTallier

  def nationalPerFirstPreferenceTallier: NormalTallier[Party]

  def perStateTallier: NormalTallier[State]

  def perDivisionTallier: NormalTallier[Division]

  def perFirstPreferencedGroupTallier: TieredTallier[State, BallotGroup]

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
      TableBuilders.PerStateTableBuilder(nationalTallier, perStateTallier, primaryCountColumnTitle),
      TableBuilders.PerDivisionTableBuilder(nationalTallier, perDivisionTallier, primaryCountColumnTitle)
    ) ++ perGroupTableBuilders
  }
}
