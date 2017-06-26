package au.id.tmm.senatedb.webapp.services

import akka.testkit.TestProbe
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.webapp.persistence.daos.HardCodedElectionDao
import au.id.tmm.senatedb.webapp.persistence.population.DbPopulationActor.{Requests, Responses}
import au.id.tmm.senatedb.webapp.services.DbPopulationService.Exceptions
import au.id.tmm.senatedb.webapp.services.exceptions.NoSuchElectionException
import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Try}

class DbPopulationServiceSpec extends ImprovedFlatSpec with MocksActor {
  private val mockDbPopulationActor = TestProbe()
  private val electionDao = new HardCodedElectionDao()

  private val testElection = SenateElection.`2016`
  private val testElectionId = electionDao.idOf(testElection).get

  private val sut = new DbPopulationService(mockDbPopulationActor.ref, electionDao)

  "a DB population service" should "indicate when an election has not been populated" in {
    val request = sut.isElectionPopulated(testElectionId)

    mockDbPopulationActor.expectMsg(Requests.IsElectionPopulated(testElection))
    mockDbPopulationActor.reply(Responses.ElectionPopulatedStatus(testElection, isPopulated = false))

    val response = await(request)

    assert(response === false)
  }

  it should "indicate when an election has been populated" in {
    val request = sut.isElectionPopulated(testElectionId)

    mockDbPopulationActor.expectMsg(Requests.IsElectionPopulated(testElection))
    mockDbPopulationActor.reply(Responses.ElectionPopulatedStatus(testElection, isPopulated = true))

    val response = await(request)

    assert(response === true)
  }

  it should "populate an election" in {
    val request = sut.beginPopulationFor(testElectionId)

    mockDbPopulationActor.expectMsg(Requests.PleasePopulateForElection(testElection, replyWhenDone = false))
    mockDbPopulationActor.reply(Responses.OkIWillPopulateElection(testElection))

    val response = await(request)

    assert(response === {})
  }

  it should "throw on a request to populate an election when another election is already being populated" in {
    val request = sut.beginPopulationFor(testElectionId)

    mockDbPopulationActor.expectMsg(Requests.PleasePopulateForElection(testElection, replyWhenDone = false))
    mockDbPopulationActor.reply(Responses.AlreadyPopulatingAnotherElection(SenateElection.`2013`))

    val response = Try(await(request))

    assert(response === Failure(Exceptions.AnotherElectionCurrentlyPopulatingException(SenateElection.`2013`)))
  }

  it should "throw when the given election doesn't exist" in {
    val response = Try(await(sut.beginPopulationFor("asdf")))

    assert(response === Failure(NoSuchElectionException("asdf")))
  }

  private def await[A](future: Future[A]) = Await.result(future, Duration.Inf)
}
