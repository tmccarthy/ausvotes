package au.id.tmm.senatedb.reporting
import au.id.tmm.senatedb.tallies.{CountSavedBallots, PredicateTallier}

object SavedBallotsReportBuilder extends StandardPredicateBasedReportBuilder {
  override def primaryCountColumnTitle: String = "Saved ballots"

  override def reportTitle: String = "Saved ballots"

  override def predicateTallier: PredicateTallier = CountSavedBallots
}
