package au.id.tmm.senatedb.api.services

import akka.testkit.TestProbe
import au.id.tmm.senatedb.api.persistence.daos.{DivisionDao, ElectionDao, StatDao}
import au.id.tmm.senatedb.api.persistence.entities.stats.{Stat, StatClass}
import au.id.tmm.senatedb.api.persistence.population.DbPopulationActor.{Requests, Responses}
import au.id.tmm.senatedb.api.services.exceptions.{NoSuchDivisionException, NoSuchStateException}
import au.id.tmm.senatedb.core.fixtures.DivisionFixture
import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.{Division, JurisdictionLevel}
import au.id.tmm.utilities.collection.Rank
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import org.scalamock.scalatest.MockFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class DivisionServiceSpec extends ImprovedFlatSpec with MocksActor with MockFactory {
  private val mockDbPopulationActor = TestProbe()
  private val divisionDao = mock[DivisionDao]
  private val statDao = mock[StatDao]

  private val testElection = SenateElection.`2016`
  private val testElectionId = ElectionDao.idOf(testElection).get

  private val sut = new DivisionService(divisionDao, statDao, mockDbPopulationActor.ref)

  private def await[A](future: Future[A]) = Await.result(future, Duration.Inf)

  "the division service" should "lookup a division by name" in {
    (divisionDao.find _)
      .expects(testElection, State.ACT, "Canberra")
      .returns(Future.successful(Some(DivisionFixture.ACT.CANBERRA)))

    val eventualDivision = sut.divisionWith("2016", "ACT", "Canberra")

    expectElectionIsPopulated()

    val division = await(eventualDivision)

    assert(division === DivisionFixture.ACT.CANBERRA)
  }

  it should "throw when the state can't be identified" in {
    val eventualDivision = sut.divisionWith("2016", "asdf", "Canberra")

    expectElectionIsPopulated()

    val exception = intercept[NoSuchStateException](await(eventualDivision))

    assert(exception === NoSuchStateException("asdf"))
  }

  it should "throw when the division can't be identified" in {
    (divisionDao.find _)
      .expects(testElection, State.ACT, "asdf")
      .returns(Future.successful(None))

    val eventualDivision = sut.divisionWith("2016", "ACT", "asdf")

    expectElectionIsPopulated()

    val exception = intercept[NoSuchDivisionException](await(eventualDivision))

    assert(exception === NoSuchDivisionException(testElection, State.ACT, "asdf"))
  }

  it should "retrieve the stats for a division" in {
    (divisionDao.find _)
      .expects(testElection, State.ACT, "Canberra")
      .returns(Future.successful(Some(DivisionFixture.ACT.CANBERRA)))

    val expectedStats = Set(
      Stat(
        statClass = StatClass.FormalBallots,
        jurisdictionLevel = JurisdictionLevel.Division,
        jurisdiction = DivisionFixture.ACT.CANBERRA,
        amount = 42d,
        Map(
          JurisdictionLevel.Nation -> Rank(1, 1, rankIsShared = false),
        ),
        perCapita = None,
        rankPerCapitaPerJurisdictionLevel = Map(),
      )
    )

    (statDao.statsFor(_: Division))
      .expects(DivisionFixture.ACT.CANBERRA)
      .returns(Future.successful(expectedStats))

    val eventualActualStats = sut.statsFor("2016", "ACT", "Canberra")

    expectElectionIsPopulated()

    val actualStats = await(eventualActualStats)

    assert(actualStats === expectedStats)
  }

  private def expectElectionIsPopulated(): Unit = {
    mockDbPopulationActor.expectMsg(Requests.IsElectionPopulated(testElection))
    mockDbPopulationActor.reply(Responses.ElectionPopulatedStatus(testElection, isPopulated = true))
  }
}
