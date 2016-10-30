package au.id.tmm.senatedb.reporting
import au.id.tmm.senatedb.tallies.{CountOneAtl, PredicateTallier}

object OneAtlReportBuilder extends StandardPredicateBasedReportBuilder {

  override def reportTitle: String = "Ballots with only '1' above the line"

  override def primaryCountColumnTitle: String = "Ballots with only '1' above the line"

  override def predicateTallier: PredicateTallier = CountOneAtl

}
