package au.id.tmm.senatedb.reporting.reports

import au.id.tmm.senatedb.computations.BallotWithFacts
import au.id.tmm.senatedb.model.parsing.Preference
import au.id.tmm.senatedb.reporting.{ReportGenerator, TallyReportGenerator}

object BallotsUsingTicksReportGenerator extends ReportGenerator with TallyReportGenerator {
  override private[reporting] def shouldCount(ballotWithFacts: BallotWithFacts): Boolean = {
    PreferenceDetectionUtilities.containsPreference(ballotWithFacts.ballot, Preference.Tick)
  }
}
