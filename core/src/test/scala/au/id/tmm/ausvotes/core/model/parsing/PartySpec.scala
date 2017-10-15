package au.id.tmm.ausvotes.core.model.parsing

import au.id.tmm.ausvotes.core.model.parsing.Party.RegisteredParty
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class PartySpec extends ImprovedFlatSpec {

  "a party" can "be transformed to the equivalent party with a canonical name" in {
    assert(RegisteredParty("Labor").canonicalise === RegisteredParty.ALP)
  }

  it can "be transformed into its national equivalent" in {
    assert(RegisteredParty("Australian Labor Party (Northern Territory) Branch").nationalEquivalent ===
      RegisteredParty("Australian Labor Party"))
  }

  "the ALP" should "not be in the Coalition" in {
    assert(RegisteredParty.ALP.inTheCoalition === false)
  }

  "the Liberal Party" should "be in the Coalition" in {
    assert(RegisteredParty.LIBERAL_PARTY_OF_AUSTRALIA.inTheCoalition === true)
  }

  "a party equivalent to the Liberal Party" should "be in the Coalition" in {
    assert(RegisteredParty("Liberal").inTheCoalition === true)
  }

  "the Nationals" should "be in the coalition" in {
    assert(RegisteredParty.THE_NATIONALS.inTheCoalition === true)
  }

  "a party whose national equivalent is the Liberal Party" should "be in the Coalition" in {
    assert(RegisteredParty("Liberal National Party of Queensland").inTheCoalition === true)
  }
}
