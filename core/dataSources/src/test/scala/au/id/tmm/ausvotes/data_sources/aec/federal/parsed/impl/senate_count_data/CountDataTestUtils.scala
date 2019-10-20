package au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.senate_count_data

import au.id.tmm.ausvotes.core.fixtures._
import au.id.tmm.ausvotes.data_sources.aec.federal.raw.FetchRawSenateDistributionOfPreferences
import au.id.tmm.ausvotes.model.federal.senate.{SenateCountData, SenateElection, SenateElectionForState, SenateGroupsAndCandidates}
import au.id.tmm.ausvotes.shared.io.test.BasicTestData
import au.id.tmm.ausvotes.shared.io.test.BasicTestData.BasicTestIO
import au.id.tmm.bfect.fs2interop._
import au.id.tmm.countstv.model.CandidateStatus.Remaining
import au.id.tmm.countstv.model.CandidateStatuses
import au.id.tmm.ausgeo.State

private[senate_count_data] final class CountDataTestUtils private(
                                                                   val state: State,
                                                                   val groupsAndCandidates: SenateGroupsAndCandidates,
                                                                   val ballotMaker: BallotMaker,
                                                                 ) {
  val senateElection: SenateElection.`2016`.type = SenateElection.`2016`
  val election: SenateElectionForState = senateElection.electionForState(state).get

  val statusesAllRemaining = CandidateStatuses(groupsAndCandidates.candidates.map(_ -> Remaining).toMap)

  private implicit val fetchRawDopRows: FetchRawSenateDistributionOfPreferences[BasicTestIO] = MockFetchRawFederalElectionData

  private implicit val fetcherUnderTest: FetchSenateCountDataFromRaw[BasicTestIO] = FetchSenateCountDataFromRaw[BasicTestIO]

  lazy val actualCountData: SenateCountData = fetcherUnderTest.senateCountDataFor(SenateElectionForState(SenateElection.`2016`, state).right.get, groupsAndCandidates)
    .runEither(BasicTestData()) match {
    case Right(countData) => countData
    case Left(exception) => throw exception
  }

}

private[senate_count_data] object CountDataTestUtils {
  lazy val ACT: CountDataTestUtils = new CountDataTestUtils(
    state = State.ACT,
    groupsAndCandidates = GroupAndCandidateFixture.ACT.groupsAndCandidates,
    ballotMaker = BallotFixture.WA.ballotMaker,
  )

  lazy val WA: CountDataTestUtils = new CountDataTestUtils(
    state = State.WA,
    groupsAndCandidates = GroupAndCandidateFixture.WA.groupsAndCandidates,
    ballotMaker = BallotFixture.WA.ballotMaker,
  )
}
