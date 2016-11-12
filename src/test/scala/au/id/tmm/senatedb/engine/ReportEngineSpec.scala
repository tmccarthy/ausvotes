package au.id.tmm.senatedb.engine

import au.id.tmm.senatedb.fixtures._
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.parsing.Party.RegisteredParty
import au.id.tmm.senatedb.reporting.OneAtlReportBuilder
import au.id.tmm.senatedb.tallies._
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import org.apache.commons.io.IOUtils

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ReportEngineSpec extends ImprovedFlatSpec {

  "the report engine" should "construct a report as expected" in {
    val ballotMaker = BallotMaker(Candidates.ACT)

    import ballotMaker.group

    val tallyEngine = MockTallyEngine.thatReturns(Tallies(
      CountFormalBallots.Nationally -> SimpleTally(42),
      CountFormalBallots.NationallyByFirstPreference -> Tally(RegisteredParty("Oranges") -> 22, RegisteredParty("Apples") -> 20),
      CountFormalBallots.ByState -> Tally(State.ACT -> 42d),
      CountFormalBallots.ByDivision -> Tally(Divisions.ACT.CANBERRA -> 42d),
      CountFormalBallots.ByFirstPreferencedGroup -> TieredTally(State.ACT -> Tally(group("C") -> 23, group("I") -> 19)),
      CountOneAtl.Nationally -> SimpleTally(32),
      CountOneAtl.NationallyByFirstPreference -> Tally(RegisteredParty("Oranges") -> 17, RegisteredParty("Apples") -> 15),
      CountOneAtl.ByState -> Tally(State.ACT -> 32d),
      CountOneAtl.ByDivision -> Tally(Divisions.ACT.CANBERRA -> 32d),
      CountOneAtl.ByFirstPreferencedGroup -> TieredTally(State.ACT -> Tally(group("C") -> 23, group("I") -> 9))
    ))

    val reportFuture = ReportEngine.runFor(
      MockParsedDataStore,
      tallyEngine,
      SenateElection.`2016`,
      Set(State.ACT),
      Set(OneAtlReportBuilder)
    )

    val actualReport = Await.result(reportFuture, Duration.Inf).head

    val expectedMarkdown = IOUtils.toString(getClass.getResourceAsStream("report_engine_spec_expected_markdown.md"), "UTF-8")

    assert(actualReport.asMarkdown === expectedMarkdown)
  }

}
