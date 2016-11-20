package au.id.tmm.senatedb.core.engine

import au.id.tmm.senatedb.core.fixtures._
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.Party.RegisteredParty
import au.id.tmm.senatedb.core.reporting.ExhaustedVotesReportBuilder
import au.id.tmm.senatedb.core.tallies._
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
      CountExhaustedVotes.Nationally -> SimpleTally(32),
      CountExhaustedVotes.NationallyByFirstPreference -> Tally(RegisteredParty("Oranges") -> 17, RegisteredParty("Apples") -> 15),
      CountExhaustedVotes.ByState -> Tally(State.ACT -> 32d),
      CountExhaustedVotes.ByDivision -> Tally(Divisions.ACT.CANBERRA -> 32d),
      CountExhaustedVotes.ByFirstPreferencedGroup -> TieredTally(State.ACT -> Tally(group("C") -> 23, group("I") -> 9))
    ))

    val reportFuture = ReportEngine.runFor(
      MockParsedDataStore,
      tallyEngine,
      SenateElection.`2016`,
      Set(State.ACT),
      Set(ExhaustedVotesReportBuilder)
    )

    val actualReport = Await.result(reportFuture, Duration.Inf).head

    val expectedMarkdown = IOUtils.toString(getClass.getResourceAsStream("report_engine_spec_expected_markdown.md"), "UTF-8")

    assert(actualReport.asMarkdown === expectedMarkdown)
  }

}
