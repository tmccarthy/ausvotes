package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.data_sources.aec.federal.parsed._
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.ballots.FetchSenateBallotsFromRaw
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.impl.senate_count_data.FetchSenateCountDataFromRaw
import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.ausvotes.model.federal.{DivisionsAndPollingPlaces, FederalElection}
import au.id.tmm.ausvotes.shared.io.test.BasicTestData
import au.id.tmm.ausvotes.shared.io.test.BasicTestData.BasicTestIO
import au.id.tmm.ausvotes.shared.io.test.TestIO.testIOIsABME
import au.id.tmm.bfect.BME
import fs2.Stream

object MockFetchFederalElectionData extends FetchDivisionsAndFederalPollingPlaces[BasicTestIO]
  with FetchSenateBallots[BasicTestIO]
  with FetchSenateCountData[BasicTestIO]
  with FetchSenateGroupsAndCandidates[BasicTestIO] {

  implicit val fetchRawData: MockFetchRawFederalElectionData.type = MockFetchRawFederalElectionData

  override def divisionsAndFederalPollingPlacesFor(election: FederalElection): BasicTestData.BasicTestIO[FetchDivisionsAndFederalPollingPlaces.Error, DivisionsAndPollingPlaces] =
    BME[BasicTestIO].pure(DivisionAndPollingPlaceFixture.ACT.divisionsAndPollingPlaces)

  override def senateBallotsFor(
                                 election: SenateElectionForState,
                                 allGroupsAndCandidates: SenateGroupsAndCandidates,
                                 divisionsAndPollingPlaces: DivisionsAndPollingPlaces,
                               ): BasicTestData.BasicTestIO[FetchSenateBallots.Error, Stream[BasicTestData.BasicTestIO[Throwable, +?], SenateBallot]] =
    FetchSenateBallotsFromRaw[BasicTestIO].senateBallotsFor(election, allGroupsAndCandidates, divisionsAndPollingPlaces)

  override def senateCountDataFor(
                                   election: SenateElectionForState,
                                   groupsAndCandidates: SenateGroupsAndCandidates,
                                 ): BasicTestData.BasicTestIO[FetchSenateCountData.Error, SenateCountData] =
    FetchSenateCountDataFromRaw[BasicTestIO].senateCountDataFor(election, groupsAndCandidates)

  override def senateGroupsAndCandidatesFor(election: SenateElection): BasicTestData.BasicTestIO[FetchSenateGroupsAndCandidates.Error, SenateGroupsAndCandidates] =
    BME[BasicTestIO].pure(GroupAndCandidateFixture.ACT.groupsAndCandidates)

  override def senateGroupsAndCandidatesFor(electionForState: SenateElectionForState): BasicTestData.BasicTestIO[FetchSenateGroupsAndCandidates.Error, SenateGroupsAndCandidates] =
    BME[BasicTestIO].pure(GroupAndCandidateFixture.ACT.groupsAndCandidates)

}
