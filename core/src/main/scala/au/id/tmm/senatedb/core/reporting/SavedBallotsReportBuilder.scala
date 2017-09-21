package au.id.tmm.senatedb.core.reporting

import au.id.tmm.senatedb.core.model.computation.SavingsProvision
import au.id.tmm.senatedb.core.reportwriting.table.Column._
import au.id.tmm.senatedb.core.reportwriting.table.{Column, TallyTable}
import au.id.tmm.senatedb.core.tallies._

object SavedBallotsReportBuilder extends StandardReportBuilder {
  override def primaryCountColumnTitle: String = "Saved ballots"

  override def reportTitle: String = "Saved ballots"

  override def ballotCounter = BallotCounter.UsedSavingsProvision

  override def tableBuilders: Vector[TableBuilder] = {

    val buildersFromSuper = super.tableBuilders

    val (leftOfExtraTable, rightOfExtraTable) =
      buildersFromSuper.splitAt(buildersFromSuper.indexOf(nationalTallyTableBuilder) + 1)

    leftOfExtraTable ++ Vector(NationallyByUsedSavingsProvisionTableBuilder) ++ rightOfExtraTable
  }

  private object NationallyByUsedSavingsProvisionTableBuilder extends TableBuilder {

    private val tallyFormalBallotsNationally = TallierBuilder
      .counting(BallotCounter.FormalBallots)
      .overall()

    private val tallySavingsProvisionsUsed = TallierBuilder
      .counting(BallotCounter.FormalBallots)
      .groupedBy(BallotGrouping.UsedSavingsProvision)

    private val tallyBallotsUsingSavingsProvisions = TallierBuilder.counting(BallotCounter.UsedSavingsProvision).overall()

    override def requiredTalliers: Set[Tallier] = Set(
      tallyFormalBallotsNationally,
      tallySavingsProvisionsUsed,
      tallyBallotsUsingSavingsProvisions
    )

    override def tableFrom(tallies: TallyBundle): TallyTable[_] = {
      val totalFormalBallots = tallies.tallyProducedBy(tallyFormalBallotsNationally)
      val totalSavedBallots = tallies.tallyProducedBy(tallyBallotsUsingSavingsProvisions)
      val totalBallotsPerSavingsProvision = tallies.tallyProducedBy(tallySavingsProvisionsUsed)

      val columns: Vector[Column] = Vector(
        SavingsProvisionNameColumn,
        PrimaryCountColumn("Ballots saved by provision"),
        FractionColumn()
      )

      TallyTable[SavingsProvision](
        totalBallotsPerSavingsProvision,
        _ => totalFormalBallots.value,
        totalSavedBallots.value,
        totalFormalBallots.value,
        columns
      )
    }

    override def tableTitle: String = "Savings provision usage"
  }

}
