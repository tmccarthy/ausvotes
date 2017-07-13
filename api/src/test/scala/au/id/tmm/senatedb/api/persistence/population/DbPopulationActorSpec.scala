package au.id.tmm.senatedb.api.persistence.population

import akka.pattern.ask
import akka.util.Timeout
import au.id.tmm.senatedb.api.persistence.population.DbPopulationActor.{Requests, Responses}
import au.id.tmm.senatedb.api.services.MocksActor
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, Awaitable, Future}

class DbPopulationActorSpec extends ImprovedFlatSpec with MocksActor {

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

  it should "acknowledge a request to populate" in {
    (dbPopulator.populateAsNeeded _).expects(SenateElection.`2016`).returns(futureWithSomeWork)

    val response = await(sut ? Requests.PleasePopulateForElection(SenateElection.`2016`, replyWhenDone = false))

    assert(response === Responses.OkIWillPopulateElection(SenateElection.`2016`))
  }

  it should "reply when the population has finished if asked" in {
    (dbPopulator.populateAsNeeded _).expects(SenateElection.`2016`).returns(futureWithSomeWork)

    val response = await(sut ? Requests.PleasePopulateForElection(SenateElection.`2016`, replyWhenDone = true))

    assert(response === Responses.FinishedPopulatingFor(SenateElection.`2016`))
  }

  it can "respond that a currently populating election has not been populated" in {
    (dbPopulator.populateAsNeeded _).expects(SenateElection.`2016`).returns(futureWithSomeWork)

    val eventualResponse1 = sut ? Requests.PleasePopulateForElection(SenateElection.`2016`, replyWhenDone = true)
    val eventualResponse2 = sut ? Requests.IsElectionPopulated(SenateElection.`2016`)

    await(eventualResponse1)
    val response = await(eventualResponse2)

    assert(response === Responses.ElectionPopulatedStatus(SenateElection.`2016`, isPopulated = false))
  }

  it should "indicate that it is currently populating when it is populating for an election" in {
    (dbPopulator.populateAsNeeded _).expects(SenateElection.`2016`).returns(futureWithSomeWork)

    val eventualResponse1 = sut ? Requests.PleasePopulateForElection(SenateElection.`2016`, replyWhenDone = true)
    val eventualResponse2 = sut ? Requests.AreYouCurrentlyPopulating

    await(eventualResponse1)
    val response = await(eventualResponse2)

    assert(response === Responses.CurrentlyPopulatingForElection(Some(SenateElection.`2016`)))
  }

  it should "respond to subsequent requestors that have requested it populate the election currently being populated" in {
    (dbPopulator.populateAsNeeded _).expects(SenateElection.`2016`).returns(futureWithSomeWork)

    val eventualResponse1 = sut ? Requests.PleasePopulateForElection(SenateElection.`2016`, replyWhenDone = true)
    val eventualResponse2 = sut ? Requests.PleasePopulateForElection(SenateElection.`2016`, replyWhenDone = true)

    val response1 = await(eventualResponse1)
    val response2 = await(eventualResponse2)

    assert(response1 === Responses.FinishedPopulatingFor(SenateElection.`2016`))
    assert(response2 === Responses.FinishedPopulatingFor(SenateElection.`2016`))
  }

  it should "respond to requests to populate an election other than the one that is currently being populated" in {
    (dbPopulator.populateAsNeeded _).expects(SenateElection.`2016`).returns(futureWithSomeWork)

    val eventualResponse1 = sut ? Requests.PleasePopulateForElection(SenateElection.`2016`, replyWhenDone = true)
    val eventualResponse2 = sut ? Requests.PleasePopulateForElection(SenateElection.`2013`, replyWhenDone = false)

    await(eventualResponse1)
    val response2 = await(eventualResponse2)

    assert(response2 === Responses.AlreadyPopulatingAnotherElection(SenateElection.`2016`))
  }

  private def await[T](awaitable: Awaitable[T]): T = Await.result(awaitable, Duration.Inf)

  private def futureWithSomeWork: Future[Unit] = Future {
    Thread.sleep(200)
    Unit
  }
}
