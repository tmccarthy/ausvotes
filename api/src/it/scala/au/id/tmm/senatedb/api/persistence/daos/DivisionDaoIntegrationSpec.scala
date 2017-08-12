package au.id.tmm.senatedb.api.persistence.daos

import au.id.tmm.senatedb.api.integrationtest.PostgresService
import au.id.tmm.senatedb.core.fixtures.Divisions
import au.id.tmm.senatedb.core.model.SenateElection
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
    val divisionsToWrite = Divisions.ACT.divisions ++ Divisions.NT.divisions

    Await.result(sut.write(divisionsToWrite), Duration.Inf)

    val actualDivisions = Await.result(sut.allAtElection(SenateElection.`2016`), Duration.Inf)

    assert(actualDivisions === divisionsToWrite)
  }
}
