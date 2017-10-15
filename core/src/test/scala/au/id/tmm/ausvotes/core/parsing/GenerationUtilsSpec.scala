package au.id.tmm.ausvotes.core.parsing

import au.id.tmm.ausvotes.core.rawdata.model.FirstPreferencesRow
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class GenerationUtilsSpec extends ImprovedFlatSpec {

  "generation utils" should "correctly parse a state" in {
    val state = GenerationUtils.stateFrom("ACT",
      FirstPreferencesRow("ACT", "A", "1", 1, "Jane Doe", "Labor", 42, 42, 42, 42, 42, 42))

    assert(state === State.ACT)
  }

  it should "fail with a nice message if the state is bad" in {
    val ex = intercept[BadDataException] {
      GenerationUtils.stateFrom("BLAH",
        FirstPreferencesRow("BLAH", "A", "1", 1, "Jane Doe", "Labor", 42, 42, 42, 42, 42, 42))
    }

    assert(ex.getMessage === "Encountered bad state value BLAH in row " +
      "FirstPreferencesRow(BLAH,A,1,1,Jane Doe,Labor,42,42,42,42,42,42)")
  }
}
