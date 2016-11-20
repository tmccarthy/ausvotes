package au.id.tmm.senatedb.core.reporting
import au.id.tmm.senatedb.core.tallies.{CountSavedBallots, PredicateTallier}

object SavedBallotsReportBuilder extends StandardReportBuilder {
  override def primaryCountColumnTitle: String = "Saved ballots"

  override def reportTitle: String = "Saved ballots"

  override def perBallotTallier: PredicateTallier = CountSavedBallots
}
