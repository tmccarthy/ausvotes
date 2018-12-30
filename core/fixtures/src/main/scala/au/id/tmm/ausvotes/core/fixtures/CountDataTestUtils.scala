package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.core.model.GroupsAndCandidates
import au.id.tmm.ausvotes.core.parsing.countdata.CountDataGeneration
import au.id.tmm.ausvotes.core.rawdata.RawDataStore
import au.id.tmm.ausvotes.model.federal.senate.{SenateCountData, SenateElection, SenateElectionForState}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.resources.ManagedResourceUtils.ExtractableManagedResourceOps

final class CountDataTestUtils private(
                                        val state: State,
                                        val groupsAndCandidates: GroupsAndCandidates,
                                        val ballotMaker: BallotMaker,
                                      ) {
  private val rawDataStore = RawDataStore(MockAecResourceStore)

  val senateElection: SenateElection.`2016`.type = SenateElection.`2016`
  val election: SenateElectionForState = SenateElectionForState(senateElection, state).right.get

  lazy val countData: SenateCountData = resource.managed(rawDataStore.distributionsOfPreferencesFor(election))
    .map(CountDataGeneration.fromDistributionOfPreferencesRows(election, groupsAndCandidates, _))
    .toTry
    .get

}

object CountDataTestUtils {
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
