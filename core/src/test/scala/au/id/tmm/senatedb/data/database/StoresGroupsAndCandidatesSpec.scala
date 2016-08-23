package au.id.tmm.senatedb.data.database

import au.id.tmm.senatedb.data.TestData
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.concurrent.Await
import scala.concurrent.duration.Duration.Inf

class StoresGroupsAndCandidatesSpec extends ImprovedFlatSpec with TestsPersistence {

  "storing groups" should "put the groups in the database" in {
    val groupsToStore = TestData.allActGroups

    Await.result(persistence.storeGroups(groupsToStore), Inf)

    val storedGroups = Await.result(persistence.runQuery(persistence.dal.groups), Inf).toSet

    assert(groupsToStore === storedGroups)
  }

  "storing candidates" should "put the candidates in the database" in {
    val candidatesToStore = TestData.allNtCandidates

    Await.result(persistence.storeCandidates(candidatesToStore), Inf)

    val storedCandidates = Await.result(persistence.runQuery(persistence.dal.candidates), Inf).toSet

    assert(candidatesToStore === storedCandidates)
  }

  behaviour of "the hasGroupsFor check"

  it should "return false if no groups have been stored" in {
    val hasGroups = Await.result(persistence.hasGroupsFor(SenateElection.`2016`), Inf)

    assert(!hasGroups)
  }

  it should "return true if groups have been stored" in {
    Await.result(persistence.storeGroups(TestData.allActGroups), Inf)

    val hasGroups = Await.result(persistence.hasGroupsFor(SenateElection.`2016`), Inf)

    assert(hasGroups)
  }

  behaviour of "the hasCandidatesFor check"

  it should "return false if no groups have been stored" in {
    val hasCandidates = Await.result(persistence.hasCandidatesFor(SenateElection.`2016`), Inf)

    assert(!hasCandidates)
  }

  it should "return true if groups have been stored" in {
    Await.result(persistence.storeCandidates(TestData.allActCandidates), Inf)

    val hasCandidates = Await.result(persistence.hasCandidatesFor(SenateElection.`2016`), Inf)

    assert(hasCandidates)
  }
}
