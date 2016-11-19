package au.id.tmm.senatedb.reporting
import au.id.tmm.senatedb.tallies.{CountSavedBallots, PredicateTallier}

object SavedBallotsReportBuilder extends StandardReportBuilder {
  override def primaryCountColumnTitle: String = "Saved ballots"

  override def reportTitle: String = "Saved ballots"

  override def perBallotTallier: PredicateTallier = CountSavedBallots
}
