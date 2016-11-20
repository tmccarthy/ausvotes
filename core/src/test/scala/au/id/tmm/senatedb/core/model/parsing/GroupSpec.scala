package au.id.tmm.senatedb.core.model.parsing

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.Party.Independent
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class GroupSpec extends ImprovedFlatSpec {

  "a group" can "not use the UG code" in {
    intercept[IllegalArgumentException] {
      Group(SenateElection.`2016`, State.ACT, "UG", Independent)
    }
  }

  it should "have an index for a single-letter code" in {
    val group = Group(SenateElection.`2016`, State.ACT, "D", Independent)

    assert(group.index === 3)
  }

  it should "have an index for a double-letter code" in {
    val group = Group(SenateElection.`2016`, State.ACT, "AA", Independent)

    assert(group.index === 26)
  }
}
