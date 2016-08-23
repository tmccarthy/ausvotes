package au.id.tmm.senatedb.data.rawdatastore

import au.id.tmm.senatedb.data.TestData
import au.id.tmm.senatedb.data.rawdatastore.download.TestingRawData
import au.id.tmm.senatedb.model.{SenateElection, State}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class RawDataStoreSpec extends ImprovedFlatSpec with TestingRawData {

  val rawDataStore = RawDataStore(testingRawDataDir)

  behaviour of "the raw data store"

  it should "retrieve ballots for the NT as expected" in {
    val ballots = rawDataStore.retrieveBallots(SenateElection.`2016`, State.NT, TestData.allNtCandidates).get

    assert(ballots.size === 102027)
  }

  it should "retrieve the groups as expected" in {
    val (groups, _) = rawDataStore.retrieveGroupsAndCandidates(SenateElection.`2016`).get

    assert(groups.size === 206)
  }

  it should "retrieve the candidates as expected" in {
    val (_, candidates) = rawDataStore.retrieveGroupsAndCandidates(SenateElection.`2016`).get

    assert(candidates.size === 631)
  }

}
