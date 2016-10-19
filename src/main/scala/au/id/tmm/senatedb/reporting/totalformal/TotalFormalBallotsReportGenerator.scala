package au.id.tmm.senatedb.reporting.totalformal

import au.id.tmm.senatedb.computations.BallotFacts
import au.id.tmm.senatedb.reporting.ReportGenerator
import au.id.tmm.utilities.geo.australia.State

object TotalFormalBallotsReportGenerator extends ReportGenerator[TotalFormalBallotsReport] {
  override def generateFor(state: State, ballotFacts: Vector[BallotFacts]): TotalFormalBallotsReport =
    TotalFormalBallotsReport(ballotFacts.size)
}
