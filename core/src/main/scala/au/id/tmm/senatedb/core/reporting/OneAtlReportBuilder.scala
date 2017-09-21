package au.id.tmm.senatedb.core.reporting

import au.id.tmm.senatedb.core.tallies.BallotCounter

object OneAtlReportBuilder extends StandardReportBuilder {

  override def reportTitle: String = "Ballots with only '1' above the line"

  override def primaryCountColumnTitle: String = "Ballots with only '1' above the line"

  override def ballotCounter = BallotCounter.Voted1Atl

}
