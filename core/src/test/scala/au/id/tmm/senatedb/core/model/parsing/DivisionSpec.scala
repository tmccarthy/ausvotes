package au.id.tmm.senatedb.core.model.parsing

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class DivisionSpec extends ImprovedFlatSpec {

  "divisions" should "be sorted by election, state and name" in {
    val sturt2013 = Division(SenateElection.`2013`, State.SA, "Sturt", 1)
    val canberra2016 = Division(SenateElection.`2016`, State.ACT, "Canberra", 2)
    val sturt2016 = Division(SenateElection.`2016`, State.SA, "Sturt", 1)
    val boothby2016 = Division(SenateElection.`2016`, State.SA, "Boothby", 3)

    val divisions = Vector(
      sturt2013,
      canberra2016,
      sturt2016,
      boothby2016
    )

    val expectedOrder = Vector(
      sturt2013,
      canberra2016,
      boothby2016,
      sturt2016
    )

    assert(divisions.sorted === expectedOrder)
  }
}
