package au.id.tmm.ausvotes.core.engine

import au.id.tmm.ausvotes.core.fixtures.{GroupAndCandidateFixture, MockAecResourceStore}
import au.id.tmm.ausvotes.core.rawdata.RawDataStore
import au.id.tmm.ausvotes.model.federal.FederalElection
import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, SenateElectionForState}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ParsedDataStoreSpec extends ImprovedFlatSpec {

  private val aecResourceStore = MockAecResourceStore
  private val rawDataStore = RawDataStore(aecResourceStore)

  private val sut = ParsedDataStore(rawDataStore)

  "a ParsedDataStore" should "retrieve the groups" in {
    val groupsAndCandidates = sut.groupsAndCandidatesFor(SenateElection.`2016`)

    assert(groupsAndCandidates.groups.size === 3)
  }

  it should "retrieve the candidates" in {
    val groupsAndCandidates = sut.groupsAndCandidatesFor(SenateElection.`2016`)

    assert(groupsAndCandidates.candidates.size === 6)
  }

  it should "retrieve the divisions" in {
    val divisionsAndPollingPlaces = sut.divisionsAndPollingPlacesFor(FederalElection.`2016`)

    assert(divisionsAndPollingPlaces.divisions.size === 150)
  }

  it should "retrieve the polling places" in {
    val divisionsAndPollingPlaces = sut.divisionsAndPollingPlacesFor(FederalElection.`2016`)

    assert(divisionsAndPollingPlaces.pollingPlaces.size === 8328)
  }

  it should "retrieve the ballots" in {
    for {
      ballots <- resource.managed(sut.ballotsFor(
        election = SenateElectionForState(SenateElection.`2016`, State.ACT).right.get,
        groupsAndCandidates = GroupAndCandidateFixture.ACT.groupsAndCandidates,
        divisionsAndPollingPlaces = sut.divisionsAndPollingPlacesFor(FederalElection.`2016`),
      ))
    } {
      assert(ballots.size === 4)
    }
  }

  it should "retrieve the count data" in {
    val countData = sut.countDataFor(SenateElectionForState(SenateElection.`2016`, State.ACT).right.get, GroupAndCandidateFixture.ACT.groupsAndCandidates)

    assert(countData.completedCount.countSteps.size === 30)

  }

}
