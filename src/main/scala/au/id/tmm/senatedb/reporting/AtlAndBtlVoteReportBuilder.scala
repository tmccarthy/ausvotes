package au.id.tmm.senatedb.reporting
import au.id.tmm.senatedb.tallies.{CountAtlAndBtl, PredicateTallier}

object AtlAndBtlVoteReportBuilder extends StandardReportBuilder {
  override def primaryCountColumnTitle: String = "Ballots formal both above and below the line"

  override def reportTitle: String = "Ballots formal both above and below the line"

  override def predicateTallier: PredicateTallier = CountAtlAndBtl
}
