package au.id.tmm.senatedb.api.persistence.daos

import au.id.tmm.senatedb.api.integrationtest.PostgresService
import au.id.tmm.senatedb.core.fixtures.{Divisions, PollingPlaces}
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.flyweights.PostcodeFlyweight
import au.id.tmm.senatedb.core.model.parsing.VoteCollectionPoint
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class VoteCollectionPointDaoIntegrationSpec extends ImprovedFlatSpec with PostgresService {

  private val divisionDao = new ConcreteDivisionDao()

  private val sut = {
    val postcodeFlyweight = PostcodeFlyweight()

    new ConcreteVoteCollectionPointDao(
      new ConcreteAddressDao(postcodeFlyweight),
      divisionDao,
      postcodeFlyweight,
    )
  }

  "a vote collection point dao" should "indicate when there have been no polling places populated for an election" in {
    val vcpsToWrite = Set(VoteCollectionPoint.Absentee(SenateElection.`2016`, State.ACT, Divisions.ACT.CANBERRA, 1))
    val divisionsToWrite = vcpsToWrite.map(_.division)

    Await.result(divisionDao.write(divisionsToWrite), Duration.Inf)
    Await.result(sut.write(vcpsToWrite), Duration.Inf)

    val actual = Await.result(sut.hasAnyPollingPlacesFor(SenateElection.`2016`), Duration.Inf)

    assert(actual === false)
  }

  it should "indicate when there have been no non-polling place vcps populated for an election" in {
    val pollingPlacesToWrite = PollingPlaces.ACT.pollingPlaces ++ PollingPlaces.NT.pollingPlaces
    val divisionsToWrite = pollingPlacesToWrite.map(_.division)

    Await.result(divisionDao.write(divisionsToWrite), Duration.Inf)
    Await.result(sut.write(pollingPlacesToWrite), Duration.Inf)

    val actual = Await.result(sut.hasAnySpecialVoteCollectionPointsFor(SenateElection.`2016`), Duration.Inf)

    assert(actual === false)
  }

  it should "return all populated vcps at an election" in {
    val pollingPlacesToWrite = PollingPlaces.ACT.pollingPlaces ++ PollingPlaces.NT.pollingPlaces
    val divisionsToWrite = pollingPlacesToWrite.map(_.division)

    Await.result(divisionDao.write(divisionsToWrite), Duration.Inf)
    Await.result(sut.write(pollingPlacesToWrite), Duration.Inf)

    val storedPollingPlaces = Await.result(
      Future.sequence(divisionsToWrite.map(sut.allPollingPlacesForDivision)).map(_.flatten),
      Duration.Inf,
    )

    assert(storedPollingPlaces === pollingPlacesToWrite)
  }
}
