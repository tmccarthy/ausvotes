package au.id.tmm.ausvotes.api.persistence.daos

import au.id.tmm.ausvotes.core.fixtures.DivisionFixture
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import org.scalamock.scalatest.MockFactory

import scala.concurrent.ExecutionContext.Implicits.global

class DivisionDaoSpec extends ImprovedFlatSpec with MockFactory {

  "the division dao" should "generate a unique id for a division" in {
    val divisionDao = new ConcreteDivisionDao()

    val actualId = divisionDao.idOf(DivisionFixture.ACT.CANBERRA)
    val expectedId = 420229601

    assert(expectedId === actualId)
  }

}
