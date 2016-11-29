package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.model.computation.SavingsProvision
import au.id.tmm.senatedb.reportwriting.table.Column._
import au.id.tmm.senatedb.reportwriting.table.{Column, TallyTable}
import au.id.tmm.senatedb.tallies._

object SavedBallotsReportBuilder extends StandardReportBuilder {
  override def primaryCountColumnTitle: String = "Saved ballots"

  override def reportTitle: String = "Saved ballots"

  override def perBallotTallier: PredicateTallier = CountSavedBallots

  override def tableBuilders: Vector[TableBuilder] = {

    val buildersFromSuper = super.tableBuilders

    val (leftOfExtraTable, rightOfExtraTable) =
      buildersFromSuper.splitAt(buildersFromSuper.indexOf(nationalTallyTableBuilder) + 1)

    leftOfExtraTable ++ Vector(NationallyByUsedSavingsProvisionTableBuilder) ++ rightOfExtraTable
  }

  private object NationallyByUsedSavingsProvisionTableBuilder extends TableBuilder {

    override def requiredTalliers: Set[Tallier] = Set(
      CountFormalBallots.Nationally,
      CountBallotSavingsProvisionUsage.Nationally,
      CountSavedBallots.Nationally
    )

    override def tableFrom(tallies: Tallies): TallyTable[_] = {
      val totalFormalBallots = tallies.tallyBy(CountFormalBallots.Nationally)
      val totalSavedBallots = tallies.tallyBy(CountSavedBallots.Nationally)
      val totalBallotsPerSavingsProvision = tallies.tallyBy(CountBallotSavingsProvisionUsage.Nationally)

      val columns: Vector[Column] = Vector(
        SavingsProvisionNameColumn,
        PrimaryCountColumn("Ballots saved by provision"),
        FractionColumn()
      )

      TallyTable[SavingsProvision](
        totalBallotsPerSavingsProvision,
        _ => totalFormalBallots.count,
        totalSavedBallots.count,
        totalFormalBallots.count,
        columns
      )
    }

    override def tableTitle: String = "Savings provision usage"
  }

}
