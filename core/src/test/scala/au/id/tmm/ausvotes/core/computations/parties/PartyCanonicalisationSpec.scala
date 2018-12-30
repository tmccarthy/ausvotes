package au.id.tmm.ausvotes.core.computations.parties

import au.id.tmm.ausvotes.model.Party
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class PartyCanonicalisationSpec extends ImprovedFlatSpec {

  "a party" can "be transformed to the equivalent party with a canonical name" in {
    assert(PartyCanonicalisation.canonicalise(Party("Labor")) === Party("Australian Labor Party"))
  }

  it can "be transformed into its national equivalent" in {
    assert(PartyCanonicalisation.nationalEquivalentOf(Party("Australian Labor Party (Northern Territory) Branch")) ===
      Party("Australian Labor Party"))
  }

  "the ALP" should "not be in the Coalition" in {
    assert(PartyCanonicalisation.isInCoalition(Party("Australian Labor Party")) === false)
  }

  "the Liberal Party" should "be in the Coalition" in {
    assert(PartyCanonicalisation.isInCoalition(Party("Liberal Party of Australia")) === true)
  }

  "a party equivalent to the Liberal Party" should "be in the Coalition" in {
    assert(PartyCanonicalisation.isInCoalition(Party("Liberal")) === true)
  }

  "the Nationals" should "be in the coalition" in {
    assert(PartyCanonicalisation.isInCoalition(Party("The Nationals")) === true)
  }

  "a party whose national equivalent is the Liberal Party" should "be in the Coalition" in {
    assert(PartyCanonicalisation.isInCoalition(Party("Liberal National Party of Queensland")) === true)
  }

}
