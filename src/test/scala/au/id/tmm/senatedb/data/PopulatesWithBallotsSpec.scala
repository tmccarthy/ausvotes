package au.id.tmm.senatedb.data

import au.id.tmm.senatedb.data.database.model.BallotRow
import au.id.tmm.senatedb.data.rawdatastore.download.DataMissingDownloadDisallowedException
import au.id.tmm.senatedb.model.{SenateElection, State}
import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.concurrent.Await
import scala.concurrent.duration.Duration.Inf

class PopulatesWithBallotsSpec extends ImprovedFlatSpec with TestsPersistencePopulator {

  def assertBallotsLoadedCorrectly(): Unit = {
    val numBallots = Await.result(persistence.runQuery(persistence.dal.ballots.size), Inf)
    val numAtlPreferences = Await.result(persistence.runQuery(persistence.dal.atlPreferences.size), Inf)
    val numBtlPreferences = Await.result(persistence.runQuery(persistence.dal.btlPreferences.size), Inf)

    assert(numBallots === 102027)
    assert(numAtlPreferences === 599794)
    assert(numBtlPreferences === 130881)
  }

  behaviour of "loadBallots"

  it can "load the ballots for the NT at the 2016 election" in {
    val loadFuture = persistencePopulator.loadBallots(SenateElection.`2016`, State.NT)

    Await.result(loadFuture, Inf)

    assertBallotsLoadedCorrectly()
  }

  it should "not reload ballots if a ballot is already loaded for that election and state" in {
    val preLoadedBallots = Set(BallotRow("blah", SenateElection.`2016`.aecID, "NT", "bah", 5, 1, 1))
    Await.result(persistence.execute(persistence.dal.insertBallots(preLoadedBallots)), Inf)

    val loadFuture = persistencePopulator.loadBallots(SenateElection.`2016`, State.NT, forceReload = false)

    Await.result(loadFuture, Inf)

    val numBallots = Await.result(persistence.runQuery(persistence.dal.ballots.size), Inf)

    assert(numBallots === 1)
  }

  it should "reload the ballots if requested" in {
    val preLoadedBallots = Set(BallotRow("blah", SenateElection.`2016`.aecID, "NT", "bah", 5, 1, 1))
    Await.result(persistence.execute(persistence.dal.insertBallots(preLoadedBallots)), Inf)

    val loadFuture = persistencePopulator.loadBallots(SenateElection.`2016`, State.NT, forceReload = true)

    Await.result(loadFuture, Inf)

    assertBallotsLoadedCorrectly()
  }

  it should "fail if no raw data exists and downloading is disallowed" in {
    val loadFuture = persistencePopulator.loadBallots(SenateElection.`2016`, State.NT, allowDownloading = false)

    intercept[DataMissingDownloadDisallowedException] {
      Await.result(loadFuture, Inf)
    }
  }

  it should "get the correct number of exhausted ballots for the NT election" in {
    import persistence.dal.driver.api._

    val loadFuture = persistencePopulator.loadBallots(SenateElection.`2016`, State.NT)

    Await.result(loadFuture, Inf)

    val countExhaustedQuery = persistence.dal.ballotFacts.filter(_.exhaustedAtCount.isDefined).size

    val numExhaustedBallots = Await.result(persistence.runQuery(countExhaustedQuery), Inf)

    assert(numExhaustedBallots === 0)
  }

  ignore should "get the correct number of exhausted ballots for the TAS election" in {
    import persistence.dal.driver.api._

    val loadFuture = persistencePopulator.loadBallots(SenateElection.`2016`, State.TAS)

    Await.result(loadFuture, Inf)

    val countExhaustedQuery = persistence.dal.ballotFacts.filter(_.exhaustedAtCount.isDefined).size

    val numExhaustedBallots = Await.result(persistence.runQuery(countExhaustedQuery), Inf)

    assert(numExhaustedBallots === 9531)
  }
}
