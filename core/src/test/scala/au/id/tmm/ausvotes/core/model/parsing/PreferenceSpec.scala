package au.id.tmm.ausvotes.core.model.parsing

import au.id.tmm.utilities.testing.ImprovedFlatSpec

// TODO test this in the RawPreferenceParserSpec
class PreferenceSpec extends ImprovedFlatSpec {

  "a preference" can "be a number" in {
    assert(Preference.fromRawValue("1") contains Preference.Numbered(1))
  }

  it can "be a tick" in {
    assert(Preference.fromRawValue("/") contains Preference.Tick)
  }

  it can "be a cross" in {
    assert(Preference.fromRawValue("*") contains Preference.Cross)
  }

  it can "be missing" in {
    assert(Preference.fromRawValue("") === None)
  }

  it should "not be an invalid character" in {
    intercept[IllegalArgumentException]{
      Preference.fromRawValue("&")
    }
  }

  it should "not be an invalid combination of characters" in {
    intercept[IllegalArgumentException]{
      Preference.fromRawValue("&&&")
    }
  }
}
