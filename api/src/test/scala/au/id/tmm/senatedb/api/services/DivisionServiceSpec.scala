package au.id.tmm.senatedb.api.services

import akka.testkit.TestProbe
import au.id.tmm.senatedb.core.fixtures.Divisions
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.api.persistence.daos.{DivisionDao, HardCodedElectionDao}
import au.id.tmm.senatedb.api.persistence.entities.{DivisionStats, TotalFormalBallotsTally}
import au.id.tmm.senatedb.api.persistence.population.DbPopulationActor
import au.id.tmm.senatedb.api.services.exceptions.NoSuchElectionException
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
    val result = Try(await(sut.divisionStatsFor("asdf", "ACT", "Canberra")))

    assert(result === Failure(NoSuchElectionException("asdf")))
  }

  it should "fail if the election has not been populated" in {
    val request = sut.divisionStatsFor(testElectionId, "ACT", "Canberra")

    mockDbPopulationActor.expectMsg(DbPopulationActor.Requests.IsElectionPopulated(testElection))
    mockDbPopulationActor.reply(DbPopulationActor.Responses.ElectionPopulatedStatus(testElection, isPopulated = false))

    val result = Try(await(request))

    assert(result === Failure(RequiredElectionNotPopulatedException(testElection)))
  }

  it should "fail if no statistics can be found" in {
    val responseFromDao = Future.successful(None)

    (divisionDao.findStats _).expects(testElectionId, "ACT", "Canberra").returns(responseFromDao)

    val request = sut.divisionStatsFor(testElectionId, "ACT", "Canberra")

    mockDbPopulationActor.expectMsg(DbPopulationActor.Requests.IsElectionPopulated(testElection))
    mockDbPopulationActor.reply(DbPopulationActor.Responses.ElectionPopulatedStatus(testElection, isPopulated = true))

    val result = Try(await(request))

    assert(result === Failure(CannotFindStatsForDivision(testElection, "ACT", "Canberra")))
  }

  it should "return the found statistics" in {
    val division = Divisions.ACT.CANBERRA
    val stats = DivisionStats(Divisions.ACT.CANBERRA, TotalFormalBallotsTally(42, 1, Some(1), Some(1)))
    val responseFromDao = Future.successful(Some(stats))

    (divisionDao.findStats _).expects(testElectionId, "ACT", "Canberra").returns(responseFromDao)

    val request = sut.divisionStatsFor(testElectionId, "ACT", "Canberra")

    mockDbPopulationActor.expectMsg(DbPopulationActor.Requests.IsElectionPopulated(testElection))
    mockDbPopulationActor.reply(DbPopulationActor.Responses.ElectionPopulatedStatus(testElection, isPopulated = true))

    val result = await(request)

    assert(result === stats)
  }

  private def await[A](future: Future[A]) = Await.result(future, Duration.Inf)
}
