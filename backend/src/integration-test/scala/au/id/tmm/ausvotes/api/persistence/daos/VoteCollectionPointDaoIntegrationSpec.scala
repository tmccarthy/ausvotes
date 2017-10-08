package au.id.tmm.ausvotes.api.persistence.daos

import au.id.tmm.ausvotes.api.integrationtest.PostgresService
import au.id.tmm.ausvotes.core.fixtures.PollingPlaceFixture
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.flyweights.PostcodeFlyweight
import au.id.tmm.ausvotes.core.model.parsing.VoteCollectionPoint
import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class VoteCollectionPointDaoIntegrationSpec extends ImprovedFlatSpec with PostgresService {

  private val pollingPlaces = PollingPlaceFixture.ACT.pollingPlaces ++ PollingPlaceFixture.NT.pollingPlaces
  private val divisions = pollingPlaces.map(_.division)
  private val specialVcps = divisions.flatMap { division =>
    Set(
      VoteCollectionPoint.Absentee(division.election, division.state, division, 1),
      VoteCollectionPoint.Postal(division.election, division.state, division, 1),
      VoteCollectionPoint.PrePoll(division.election, division.state, division, 1),
      VoteCollectionPoint.Provisional(division.election, division.state, division, 1),
    )
  }

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
    Await.result(divisionDao.write(divisions), Duration.Inf)
    Await.result(sut.write(specialVcps), Duration.Inf)

    val actual = Await.result(sut.hasAnyPollingPlacesFor(SenateElection.`2016`), Duration.Inf)

    assert(actual === false)
  }

  it should "indicate when there have been no non-polling place vcps populated for an election" in {
    Await.result(divisionDao.write(divisions), Duration.Inf)
    Await.result(sut.write(pollingPlaces), Duration.Inf)

    val actual = Await.result(sut.hasAnySpecialVoteCollectionPointsFor(SenateElection.`2016`), Duration.Inf)

    assert(actual === false)
  }

  it should "return all populated vcps at an election" in {
    val vcpsToWrite = pollingPlaces ++ specialVcps

    Await.result(divisionDao.write(divisions), Duration.Inf)
    Await.result(sut.write(vcpsToWrite), Duration.Inf)

    val storedPollingPlaces = Await.result(
      Future.sequence(divisions.map(sut.allForDivision)).map(_.flatten),
      Duration.Inf,
    )

    assert(storedPollingPlaces === vcpsToWrite)
  }
}
