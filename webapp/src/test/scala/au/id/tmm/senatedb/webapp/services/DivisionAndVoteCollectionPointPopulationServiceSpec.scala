package au.id.tmm.senatedb.webapp.services

import au.id.tmm.senatedb.core.engine.ParsedDataStore
import au.id.tmm.senatedb.core.fixtures.{Divisions, MockParsedDataStore, PollingPlaces}
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.webapp.persistence.daos.{DivisionDao, VoteCollectionPointDao}
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import org.scalamock.scalatest.MockFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class DivisionAndVoteCollectionPointPopulationServiceSpec extends ImprovedFlatSpec with MockFactory {

  private val dataStore: ParsedDataStore = MockParsedDataStore
  private val divisionDao: DivisionDao = mock[DivisionDao]
  private val voteCollectionPointDao: VoteCollectionPointDao = mock[VoteCollectionPointDao]

  private val sut = new DivisionAndVoteCollectionPointPopulationService(divisionDao, voteCollectionPointDao, dataStore)

  "the division and vote collection point population service" should
    "populate the dao with divisions and polling places from the parsed data store" in {
    val election = SenateElection.`2016`

    (divisionDao.write _)
      .expects(Divisions.ACT.divisions)

    (voteCollectionPointDao.write _)
      .expects(PollingPlaces.ACT.pollingPlaces)

    Await.ready(sut.populateDivisionsAndVoteCollectionPointsFor(election), Duration.Inf)
  }

}
