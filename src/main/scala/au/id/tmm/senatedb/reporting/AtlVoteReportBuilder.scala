package au.id.tmm.senatedb.reporting
import au.id.tmm.senatedb.tallies.{CountAtl, PredicateTallier}

object AtlVoteReportBuilder extends StandardPredicateBasedReportBuilder {
  override def primaryCountColumnTitle: String = "Votes above the line"

  override def reportTitle: String = "Votes above the line"

  override def predicateTallier: PredicateTallier = CountAtl
}
