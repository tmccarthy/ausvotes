package au.id.tmm.senatedb.api.persistence.daos

import au.id.tmm.senatedb.api.integrationtest.{PostgresService, SimpleCacheApi}
import au.id.tmm.senatedb.core.fixtures.PollingPlaces
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.flyweights.PostcodeFlyweight
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import scalikejdbc.DB

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class VoteCollectionPointDaoSpec extends ImprovedFlatSpec with PostgresService {

  private val dbStructureCache = new ConcreteDbStructureCache(new SimpleCacheApi())
  private val divisionDao = new ConcreteDivisionDao(dbStructureCache)

  private val sut = {
    val postcodeFlyweight = PostcodeFlyweight()

    new ConcreteVoteCollectionPointDao(
      new ConcreteAddressDao(postcodeFlyweight),
      divisionDao,
      dbStructureCache,
      postcodeFlyweight,
    )
  }

  "a vote collection point dao" should "indicate when there have been no polling places populated for an election" in {
    val actual = Await.result(sut.hasAnyPollingPlacesFor(SenateElection.`2016`), Duration.Inf)

    assert(actual === false)
  }

  it should "indicate when there have been no non-polling place vcps populated for an election" in {
    val actual = Await.result(sut.hasAnyNonPollingPlaceVoteCollectionPointsFor(SenateElection.`2016`), Duration.Inf)

    assert(actual === false)
  }

  it should "return all populated vcps at an election" in {
    val pollingPlacesToWrite = PollingPlaces.ACT.pollingPlaces ++ PollingPlaces.NT.pollingPlaces
    val divisionsToWrite = pollingPlacesToWrite.map(_.division)

    Await.result(divisionDao.write(divisionsToWrite), Duration.Inf)
    Await.result(sut.write(pollingPlacesToWrite), Duration.Inf)

    DB.localTx { implicit session =>
      val idPerVcp = sut.idPerVoteCollectionPointInSession(SenateElection.`2016`)

      assert(idPerVcp.keySet === pollingPlacesToWrite)
    }
  }
}
