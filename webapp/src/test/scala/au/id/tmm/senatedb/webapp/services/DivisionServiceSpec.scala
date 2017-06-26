package au.id.tmm.senatedb.webapp.services

import akka.testkit.TestProbe
import au.id.tmm.senatedb.core.fixtures.Divisions
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.webapp.persistence.daos.{DivisionDao, HardCodedElectionDao}
import au.id.tmm.senatedb.webapp.persistence.entities.{DivisionStats, TotalFormalBallotsTally}
import au.id.tmm.senatedb.webapp.persistence.population.DbPopulationActor
import au.id.tmm.senatedb.webapp.services.exceptions.NoSuchElectionException
import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Try}

class DivisionServiceSpec extends ImprovedFlatSpec with MocksActor {
  private val mockDbPopulationActor = TestProbe()
  private val divisionDao = mock[DivisionDao]
  private val electionDao = new HardCodedElectionDao()

  private val testElection = SenateElection.`2016`
  private val testElectionId = electionDao.idOf(testElection).get

  private val sut = new DivisionService(electionDao, divisionDao, mockDbPopulationActor.ref)

  "a division service" should "fail if the election is invalid" in {
    val result = Try(await(sut.divisionWithStatsFor("asdf", "ACT", "Canberra")))

    assert(result === Failure(NoSuchElectionException("asdf")))
  }

  it should "fail if the election has not been populated" in {
    val request = sut.divisionWithStatsFor(testElectionId, "ACT", "Canberra")

    mockDbPopulationActor.expectMsg(DbPopulationActor.Requests.IsElectionPopulated(testElection))
    mockDbPopulationActor.reply(DbPopulationActor.Responses.ElectionPopulatedStatus(testElection, isPopulated = false))

    val result = Try(await(request))

    assert(result === Failure(RequiredElectionNotPopulatedException(testElection)))
  }

  it should "fail if no statistics can be found" in {
    val responseFromDao = Future.successful(None)

    (divisionDao.findWithStats _).expects(testElectionId, "ACT", "Canberra").returns(responseFromDao)

    val request = sut.divisionWithStatsFor(testElectionId, "ACT", "Canberra")

    mockDbPopulationActor.expectMsg(DbPopulationActor.Requests.IsElectionPopulated(testElection))
    mockDbPopulationActor.reply(DbPopulationActor.Responses.ElectionPopulatedStatus(testElection, isPopulated = true))

    val result = Try(await(request))

    assert(result === Failure(CannotFindStatsForDivision(testElection, "ACT", "Canberra")))
  }

  it should "return the found statistics" in {
    val division = Divisions.ACT.CANBERRA
    val stats = DivisionStats(TotalFormalBallotsTally(Divisions.ACT.CANBERRA, 42, 1, Some(1), Some(1)))
    val responseFromDao = Future.successful(Some((division, stats)))

    (divisionDao.findWithStats _).expects(testElectionId, "ACT", "Canberra").returns(responseFromDao)

    val request = sut.divisionWithStatsFor(testElectionId, "ACT", "Canberra")

    mockDbPopulationActor.expectMsg(DbPopulationActor.Requests.IsElectionPopulated(testElection))
    mockDbPopulationActor.reply(DbPopulationActor.Responses.ElectionPopulatedStatus(testElection, isPopulated = true))

    val result = await(request)

    assert(result === (division, stats))
  }

  private def await[A](future: Future[A]) = Await.result(future, Duration.Inf)
}
