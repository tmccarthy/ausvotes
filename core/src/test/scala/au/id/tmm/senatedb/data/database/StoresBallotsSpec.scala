package au.id.tmm.senatedb.data.database

import au.id.tmm.senatedb.data.{BallotWithPreferences, TestData}
import au.id.tmm.senatedb.model.{SenateElection, State}
import au.id.tmm.utilities.collection.CloseableIterator.IterableConstruction
import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.concurrent.Await
import scala.concurrent.duration.Duration.Inf

class StoresBallotsSpec extends ImprovedFlatSpec with TestsPersistence {

  "storing ballots" should "store the ballots in the database" in {
    val ballotWithPreferences = TestData.aTestBallot
    val ballotId = ballotWithPreferences.ballot.ballotId

    Await.result(persistence.storeBallotData(Set(ballotWithPreferences).toCloseableIterator), Inf)

    val storedBallot = Await.result(persistence.runQuery(persistence.dal.ballotsWithId(ballotId)), Inf).head
    val storedAtlPreferences = Await.result(persistence.runQuery(persistence.dal.atlPreferencesFor(ballotId)), Inf).toSet
    val storedBtlPreferences = Await.result(persistence.runQuery(persistence.dal.btlPreferencesFor(ballotId)), Inf).toSet

    val storedBallotWithPreferences = BallotWithPreferences(storedBallot, storedAtlPreferences, storedBtlPreferences)

    assert(storedBallotWithPreferences === ballotWithPreferences)
  }

  behaviour of "the hasBallotsFor check"

  it should "return false when there've been no ballots loaded" in {
    val hasBallots = Await.result(persistence.hasBallotsFor(SenateElection.`2016`, State.NT), Inf)

    assert(!hasBallots)
  }

  it should "return true when ballots have been loaded" in {
    val ballotWithPreferences = TestData.aTestBallot

    Await.result(persistence.storeBallotData(Set(ballotWithPreferences).toCloseableIterator), Inf)

    val hasBallots = Await.result(persistence.hasBallotsFor(SenateElection.`2016`, State.ACT), Inf)

    assert(hasBallots)
  }

}
