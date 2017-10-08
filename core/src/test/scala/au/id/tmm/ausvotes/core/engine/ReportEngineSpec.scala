package au.id.tmm.ausvotes.core.engine

import au.id.tmm.ausvotes.core.fixtures._
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.Party.RegisteredParty
import au.id.tmm.ausvotes.core.reporting.ExhaustedVotesReportBuilder
import au.id.tmm.ausvotes.core.tallies._
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import org.apache.commons.io.IOUtils

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ReportEngineSpec extends ImprovedFlatSpec {

  "the report engine" should "construct a report as expected" in {
    val ballotMaker = BallotMaker(CandidateFixture.ACT)

    import ballotMaker.group

    val countFormalBallots = TallierBuilder.counting(BallotCounter.FormalBallots)
    val countExhaustedVotes = TallierBuilder.counting(BallotCounter.ExhaustedVotes)

    val tallyEngine = MockTallyEngine.thatReturns(TallyBundle(
      countFormalBallots.overall() -> Tally0(42),
      countFormalBallots.groupedBy(BallotGrouping.FirstPreferencedPartyNationalEquivalent) -> Tally1(RegisteredParty("Oranges") -> 22, RegisteredParty("Apples") -> 20),
      countFormalBallots.groupedBy(BallotGrouping.State) -> Tally1(State.ACT -> 42d),
      countFormalBallots.groupedBy(BallotGrouping.Division) -> Tally1(DivisionFixture.ACT.CANBERRA -> 42d),
      countFormalBallots.groupedBy(BallotGrouping.State, BallotGrouping.FirstPreferencedGroup) -> Tally2(State.ACT -> Tally1(group("C") -> 23, group("I") -> 19)),
      countExhaustedVotes.overall() -> Tally0(32),
      countExhaustedVotes.groupedBy(BallotGrouping.FirstPreferencedPartyNationalEquivalent) -> Tally1(RegisteredParty("Oranges") -> 17, RegisteredParty("Apples") -> 15),
      countExhaustedVotes.groupedBy(BallotGrouping.State) -> Tally1(State.ACT -> 32d),
      countExhaustedVotes.groupedBy(BallotGrouping.Division) -> Tally1(DivisionFixture.ACT.CANBERRA -> 32d),
      countExhaustedVotes.groupedBy(BallotGrouping.State, BallotGrouping.FirstPreferencedGroup) -> Tally2(State.ACT -> Tally1(group("C") -> 23, group("I") -> 9))
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
