package au.id.tmm.senatedb.data

import au.id.tmm.senatedb.data.Persistence.{FormalPreferences, GroupsAndCandidates}
import au.id.tmm.senatedb.model.{SenateElection, State}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.concurrent.Await
import scala.concurrent.duration.Duration.Inf

class PersistenceSpec extends ImprovedFlatSpec with TestingPersistence {

  import persistence.dal.driver.api._

  behaviour of "the load method"

  it should "correctly load ACT groups into an in-memory database" in {
    val loadGroupsAction = persistence.load(Set(GroupsAndCandidates(SenateElection.`2016`)))

    Await.result(loadGroupsAction, Inf)

    val getStoredGroupsAction = persistence.database.run(persistence.dal.groups.result)
    val storedActGroups = Await.result(getStoredGroupsAction, Inf).toStream
      .filter(_.state == State.ACT.shortName)
      .toSet

    assert(storedActGroups === TestDataFromAct.allActGroups)
  }

  it should "correctly load ACT candidates into an in-memory database" in {
    val loadCandidatesAction = persistence.load(Set(GroupsAndCandidates(SenateElection.`2016`)))

    Await.result(loadCandidatesAction, Inf)

    val getStoredCandidatesAction = persistence.database.run(persistence.dal.candidates.result)
    val storedActCandidates = Await.result(getStoredCandidatesAction, Inf).toStream
      .filter(_.state == State.ACT.shortName)
      .toSet

    assert(storedActCandidates === TestDataFromAct.allActCandidates)
  }

  it should "correctly load NT ballots into an in-memory database" in {
    val loadAction = persistence.load(
      Set(GroupsAndCandidates(SenateElection.`2016`), FormalPreferences(SenateElection.`2016`, State.NT))
    )

    Await.result(loadAction, Inf)

    val countStoredBallotsAction = persistence.database.run(persistence.dal.ballots.size.result)

    val actualNumBallots = Await.result(countStoredBallotsAction, Inf)

    assert(102027 === actualNumBallots)
  }

  it should "correctly load above-the-line NT preferences into an in-memory database" in {
    val loadAction = persistence.load(
      Set(GroupsAndCandidates(SenateElection.`2016`), FormalPreferences(SenateElection.`2016`, State.NT))
    )

    Await.result(loadAction, Inf)

    val countStoredAtlPreferencesAction = persistence.database.run(persistence.dal.atlPreferences.size.result)

    val actualNumAtlPreferences = Await.result(countStoredAtlPreferencesAction, Inf)

    assert(606742 === actualNumAtlPreferences)
  }

  it should "correctly load NT below-the-line preferences into an in-memory database" in {
    val loadAction = persistence.load(
      Set(GroupsAndCandidates(SenateElection.`2016`), FormalPreferences(SenateElection.`2016`, State.NT))
    )

    Await.result(loadAction, Inf)

    val countStoredBtlPreferencesAction = persistence.database.run(persistence.dal.btlPreferences.size.result)

    val actualNumBtlPreferences = Await.result(countStoredBtlPreferencesAction, Inf)

    assert(123933 === actualNumBtlPreferences)
  }

  it should "throw if attempting to load NT ballots into a database without ACT groups and candidates" in {
    val loadBallotsAction = persistence.load(Set(FormalPreferences(SenateElection.`2016`, State.NT)))

    intercept[IllegalStateException] {
      Await.result(loadBallotsAction, Inf)
    }
  }
}
