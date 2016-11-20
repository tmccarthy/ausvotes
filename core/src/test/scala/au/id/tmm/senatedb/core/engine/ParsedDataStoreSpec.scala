package au.id.tmm.senatedb.core.engine

import au.id.tmm.senatedb.core.fixtures.{GroupsAndCandidates, MockAecResourceStore}
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.rawdata.RawDataStore
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
    val divisionsAndPollingPlaces = sut.divisionsAndPollingPlacesFor(SenateElection.`2016`)

    assert(divisionsAndPollingPlaces.divisions.size === 150)
  }

  it should "retrieve the polling places" in {
    val divisionsAndPollingPlaces = sut.divisionsAndPollingPlacesFor(SenateElection.`2016`)

    assert(divisionsAndPollingPlaces.pollingPlaces.size === 8328)
  }

  it should "retrive the ballots" in {
    for {
      ballots <- resource.managed(sut.ballotsFor(
        election = SenateElection.`2016`,
        groupsAndCandidates = GroupsAndCandidates.ACT.groupsAndCandidates,
        divisionsAndPollingPlaces = sut.divisionsAndPollingPlacesFor(SenateElection.`2016`),
        state = State.ACT)
      )
    } {
      assert(ballots.size === 4)
    }
  }

  it should "retrieve the count data" in {
    val countData = sut.countDataFor(SenateElection.`2016`, GroupsAndCandidates.ACT.groupsAndCandidates, State.ACT)

    assert(countData.distributionSteps.size === 28)

  }

}
