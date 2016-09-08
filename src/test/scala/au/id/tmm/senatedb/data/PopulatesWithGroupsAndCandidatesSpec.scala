package au.id.tmm.senatedb.data

import au.id.tmm.senatedb.data.database.model.GroupsRow
import au.id.tmm.senatedb.data.rawdatastore.download.DataMissingDownloadDisallowedException
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.concurrent.Await
import scala.concurrent.duration.Duration.Inf

class PopulatesWithGroupsAndCandidatesSpec extends ImprovedFlatSpec with TestsPersistencePopulator {

  def assertGroupsLoadedCorrectly(): Unit = {
    val actualNumGroups = Await.result(persistence.runQuery(persistence.dal.groups.size), Inf)

    assert(206 === actualNumGroups)
  }

  def assertCandidatesLoadedCorrectly(): Unit = {
    val actualNumCandidates = Await.result(persistence.runQuery(persistence.dal.candidates.size), Inf)

    assert(631 === actualNumCandidates)
  }

  behaviour of "loadGroupsAndCandidates"

  it can "load the groups from the 2016 election into an empty persistence" in {
    val loadFuture = persistencePopulator.loadGroupsAndCandidates(SenateElection.`2016`)

    Await.result(loadFuture, Inf)

    assertGroupsLoadedCorrectly()
  }

  it can "load the candidates from the 2016 election into an empty persistence" in {
    val loadFuture = persistencePopulator.loadGroupsAndCandidates(SenateElection.`2016`)

    Await.result(loadFuture, Inf)

    assertCandidatesLoadedCorrectly()
  }

  it should "not reload groups or candidates if a candidate or group is already loaded from that election" in {
    val preLoadedGroups = Set(GroupsRow("A", SenateElection.`2016`.aecID, "NT", "None"))
    Await.result(persistence.execute(persistence.dal.insertGroups(preLoadedGroups)), Inf)

    val loadFuture = persistencePopulator.loadGroupsAndCandidates(SenateElection.`2016`, forceReload = false)

    Await.result(loadFuture, Inf)

    val actualNumGroups = Await.result(persistence.runQuery(persistence.dal.groups.size), Inf)

    assert(1 === actualNumGroups)
  }

  it should "reload the groups if requested" in {
    val preLoadedGroups = Set(GroupsRow("A", SenateElection.`2016`.aecID, "NT", "None"))
    Await.result(persistence.execute(persistence.dal.insertGroups(preLoadedGroups)), Inf)

    val loadFuture = persistencePopulator.loadGroupsAndCandidates(SenateElection.`2016`, forceReload = true)

    Await.result(loadFuture, Inf)

    assertGroupsLoadedCorrectly()
  }

  it should "fail if no raw data exists and downloading is disallowed" in {
    val loadFuture = persistencePopulator.loadGroupsAndCandidates(SenateElection.`2016`, allowDownloading = false)

    intercept[DataMissingDownloadDisallowedException] {
      Await.result(loadFuture, Inf)
    }
  }
}
