package au.id.tmm.senatedb.model.parsing

import au.id.tmm.utilities.testing.ImprovedFlatSpec

class NameSpec extends ImprovedFlatSpec {
  "a name" can "be parsed from the comma separated form used by the AEC" in {
    assert(Name.parsedFrom("LANE, Jane") === Name("Jane", "LANE"))
  }

  "two names" can "be compared ignoring case" in {
    assert(Name("Jane", "LANE") equalsIgnoreCase Name("JANE", "Lane"))
  }

  they can "be found unequal, ignoring case" in {
    assert(!(Name("Jane", "LANE") equalsIgnoreCase Name("Daria", "Morgandorfer")))
  }
}
