package au.id.tmm.senatedb.reporting.reports

import au.id.tmm.senatedb.computations.BallotWithFacts
import au.id.tmm.senatedb.reporting.{ReportGenerator, TallyReportGenerator}

object DonkeyVoteReportGenerator extends ReportGenerator with TallyReportGenerator {
  override private[reporting] def shouldCount(ballot: BallotWithFacts): Boolean = ballot.isDonkeyVote
}
