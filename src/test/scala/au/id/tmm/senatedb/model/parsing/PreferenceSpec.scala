package au.id.tmm.senatedb.model.parsing

import au.id.tmm.utilities.testing.ImprovedFlatSpec

class PreferenceSpec extends ImprovedFlatSpec {

  "a preference" can "be a number" in {
    assert(Preference("1") === Preference.Numbered(1))
  }

  it can "be a tick" in {
    assert(Preference("/") === Preference.Tick)
  }

  it can "be a cross" in {
    assert(Preference("*") === Preference.Cross)
  }

  it can "be missing" in {
    assert(Preference("") === Preference.Missing)
  }

  it should "not be an invalid character" in {
    intercept[IllegalArgumentException]{
      Preference("&")
    }
  }

  it should "not be an invalid combination of characters" in {
    intercept[IllegalArgumentException]{
      Preference("&&&")
    }
  }
}
