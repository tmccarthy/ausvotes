package au.id.tmm.senatedb.model.parsing

import au.id.tmm.utilities.testing.ImprovedFlatSpec

class PartySpec extends ImprovedFlatSpec {

  "a party" can "be transformed to the equivalent party with a canonical name" in {
    assert(RegisteredParty("Labor").canonicalise === RegisteredParty("Australian Labor Party"))
  }

  it can "be transformed into its national equivalent" in {
    assert(RegisteredParty("Australian Labor Party (Northern Territory) Branch").nationalEquivalent ===
      RegisteredParty("Australian Labor Party"))
  }
}
