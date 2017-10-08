package au.id.tmm.ausvotes.api.persistence.daos

import au.id.tmm.ausvotes.api.integrationtest.PostgresService
import au.id.tmm.ausvotes.core.fixtures.DivisionFixture
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class DivisionDaoIntegrationSpec extends ImprovedFlatSpec with PostgresService {

  private val sut = new ConcreteDivisionDao()

  "the division dao" should "indicate when there are no populated divisions for an election" in {
    val result = Await.result(sut.hasAnyDivisionsFor(SenateElection.`2016`), Duration.Inf)

    assert(result === false)
  }

  it should "store divisions for an election" in {
    val divisionsToWrite = DivisionFixture.ACT.divisions ++ DivisionFixture.NT.divisions

    Await.result(sut.write(divisionsToWrite), Duration.Inf)

    val actualDivisions = Await.result(sut.allAtElection(SenateElection.`2016`), Duration.Inf)

    assert(actualDivisions === divisionsToWrite)
  }

  it should "be able to find a division by name" in {
    val writtenDivision = DivisionFixture.ACT.CANBERRA

    Await.result(sut.write(Set(writtenDivision)), Duration.Inf)

    val foundDivision = Await.result(sut.find(SenateElection.`2016`, State.ACT, "CanBerRa"), Duration.Inf)

    assert(foundDivision === Some(writtenDivision))
  }

  it should "return nothing when asked for a division that was not loaded" in {
    val foundDivision = Await.result(sut.find(SenateElection.`2016`, State.ACT, "CanRerRa"), Duration.Inf)

    assert(foundDivision === None)
  }
}
