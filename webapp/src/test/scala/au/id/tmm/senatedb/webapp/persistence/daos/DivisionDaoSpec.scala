package au.id.tmm.senatedb.webapp.persistence.daos

import au.id.tmm.senatedb.core.fixtures.Divisions
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class DivisionDaoSpec extends ImprovedFlatSpec {

  "the division dao" should "generate a unique id for a division" in {
    val divisionDao = new ConcreteDivisionDao(new HardCodedElectionDao)

    val actualId = divisionDao.idOf(Divisions.ACT.CANBERRA)
    val expectedId = 420229601

    assert(expectedId === actualId)
  }

}
