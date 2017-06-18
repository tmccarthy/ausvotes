package au.id.tmm.senatedb.webapp.persistence.population

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.{TestKit, TestKitBase}
import akka.util.Timeout
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.webapp.persistence.population.DbPopulationActor.{Requests, Responses}
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, Awaitable, Future}

class DbPopulationActorSpec extends ImprovedFlatSpec with MockFactory with TestKitBase with BeforeAndAfterAll {
  override implicit lazy val system = ActorSystem()
  implicit val askTimeout = Timeout(5.seconds)

  private val dbPopulator = mock[DbPopulator]

  private val sut = system.actorOf(DbPopulationActor.props(dbPopulator))

  "the DB population actor" can "indicate that it is not currently populating" in {
    val response = await(sut ? Requests.AreYouCurrentlyPopulating)

    assert(response === Responses.CurrentlyPopulatingForElection(None))
  }

  it can "respond that an election has not been populated" in {
    (dbPopulator.isPopulatedFor _).expects(SenateElection.`2016`).returns(Future.successful(false))

    val response = await(sut ? Requests.IsElectionPopulated(SenateElection.`2016`))

    assert(response === Responses.ElectionPopulatedStatus(SenateElection.`2016`, isPopulated = false))
  }

  it can "respond that an election has been populated" in {
    (dbPopulator.isPopulatedFor _).expects(SenateElection.`2016`).returns(Future.successful(true))

    val response = await(sut ? Requests.IsElectionPopulated(SenateElection.`2016`))

    assert(response === Responses.ElectionPopulatedStatus(SenateElection.`2016`, isPopulated = true))
  }

  it can "respond that the requested election is currently being populated" in {
    (dbPopulator.populateAsNeeded _).expects(SenateElection.`2016`).returns(Future {Unit})

    val populationCompleteFuture = sut ? Requests.PleasePopulateForElection(SenateElection.`2016`)

    val response = await(sut ? Requests.IsElectionPopulated(SenateElection.`2016`))

    assert(response === Responses.CurrentlyPopulatingForElection(Some(SenateElection.`2016`)))

    await(populationCompleteFuture)
  }

  it should "respond to the original requestor when it finishes populating" in {
    (dbPopulator.populateAsNeeded _).expects(SenateElection.`2016`).returns(Future.successful(Unit))

    val response = await(sut ? Requests.PleasePopulateForElection(SenateElection.`2016`))

    assert(response === Responses.FinishedPopulatingFor(SenateElection.`2016`))
  }

  it should "indicate that it is currently populating when it is populating for an election" in {
    (dbPopulator.populateAsNeeded _).expects(SenateElection.`2016`).returns(Future {Unit})

    val populationCompleteFuture = sut ? Requests.PleasePopulateForElection(SenateElection.`2016`)

    val response = await(sut ? Requests.AreYouCurrentlyPopulating)

    assert(response === Responses.CurrentlyPopulatingForElection(Some(SenateElection.`2016`)))

    await(populationCompleteFuture)
  }

  it should "respond to subsequent requestors that have requested it populate the election currently being populated" in {
    val eventualSuccessfulPopulation: Future[Unit] = Future {
      Thread.sleep(200)
      Unit
    }

    (dbPopulator.populateAsNeeded _).expects(SenateElection.`2016`).returns(eventualSuccessfulPopulation)

    val eventualResponse1 = sut ? Requests.PleasePopulateForElection(SenateElection.`2016`)
    val eventualResponse2 = sut ? Requests.PleasePopulateForElection(SenateElection.`2016`)

    val response1 = await(eventualResponse1)
    val response2 = await(eventualResponse2)

    assert(response1 === Responses.FinishedPopulatingFor(SenateElection.`2016`))
    assert(response2 === Responses.FinishedPopulatingFor(SenateElection.`2016`))
  }

  it should "respond to requests to populate an election other than the one that is currently being populated" in {
    val eventualSuccessfulPopulation: Future[Unit] = Future {
      Thread.sleep(200)
      Unit
    }

    (dbPopulator.populateAsNeeded _).expects(SenateElection.`2016`).returns(eventualSuccessfulPopulation)

    val eventualResponse1 = sut ? Requests.PleasePopulateForElection(SenateElection.`2016`)
    val eventualResponse2 = sut ? Requests.PleasePopulateForElection(SenateElection.`2013`)

    await(eventualResponse1)
    val response2 = await(eventualResponse2)

    assert(response2 === Responses.AlreadyPopulatingAnotherElection)
  }

  override protected def afterAll(): Unit = {
    super.afterAll()

    TestKit.shutdownActorSystem(system)
  }

  private def await[T](awaitable: Awaitable[T]): T = Await.result(awaitable, Duration.Inf)
}
