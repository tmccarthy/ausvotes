package au.id.tmm.ausvotes.core.computations.parties

import au.id.tmm.ausvotes.model.Party
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class PartyCanonicalisationSpec extends ImprovedFlatSpec {

  "a party" can "be transformed to the equivalent party with a canonical name" in {
    assert(Party("Labor").canonicalise === Party.ALP)
  }

  it can "be transformed into its national equivalent" in {
    assert(Party("Australian Labor Party (Northern Territory) Branch").nationalEquivalent ===
      Party("Australian Labor Party"))
  }

  "the ALP" should "not be in the Coalition" in {
    assert(Party.ALP.inTheCoalition === false)
  }

  "the Liberal Party" should "be in the Coalition" in {
    assert(Party.LIBERAL_PARTY_OF_AUSTRALIA.inTheCoalition === true)
  }

  "a party equivalent to the Liberal Party" should "be in the Coalition" in {
    assert(Party("Liberal").inTheCoalition === true)
  }

  "the Nationals" should "be in the coalition" in {
    assert(Party.THE_NATIONALS.inTheCoalition === true)
  }

  "a party whose national equivalent is the Liberal Party" should "be in the Coalition" in {
    assert(Party("Liberal National Party of Queensland").inTheCoalition === true)
  }

}
