package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.reporting.TableBuilders.{NationalPerFirstPrefTableBuilder, NationallyPerPartyTypeTableBuilder}
import au.id.tmm.senatedb.tallies.{CountExhaustedVotes, PerBallotTallier}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ExhaustedVotesReportBuilderSpec extends ImprovedFlatSpec with TestsStandardReportBuilder {
  override def expectedReportTitle: String = "Exhausted votes"

  override def expectedPredicateTallier: PerBallotTallier = CountExhaustedVotes

  override def expectedPrimaryCountColumnTitle: String = "Exhausted votes"

  override def sut: StandardReportBuilder = ExhaustedVotesReportBuilder

  it should "include a table by first preferenced party type after the first preferences table" in {
    val indexOfFirstPrefTable = sut.tableBuilders.indexWhere(_.isInstanceOf[NationalPerFirstPrefTableBuilder])
    val indexOfFirstPrefTypeTable = sut.tableBuilders.indexWhere(_.isInstanceOf[NationallyPerPartyTypeTableBuilder])

    assert(indexOfFirstPrefTypeTable === indexOfFirstPrefTable + 1)
  }
}
