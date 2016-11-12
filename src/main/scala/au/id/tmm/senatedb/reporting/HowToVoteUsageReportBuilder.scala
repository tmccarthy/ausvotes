package au.id.tmm.senatedb.reporting
import au.id.tmm.senatedb.tallies.{CountHowToVoteUsage, PredicateTallier}

object HowToVoteUsageReportBuilder extends StandardReportBuilder {
  override def primaryCountColumnTitle: String = "Ballots matching an HTV card"

  override def reportTitle: String = "How to vote card usage"

  override def perBallotTallier: PredicateTallier = CountHowToVoteUsage
}
