package au.id.tmm.ausvotes.core.computations.parties

import au.id.tmm.ausvotes.model.Party
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class PartyEquivalenceComputationSpec extends ImprovedFlatSpec {

  "a party" can "be transformed into its national equivalent" in {
    assert(PartyEquivalenceComputation.nationalEquivalentOf(Party("Australian Labor Party (Northern Territory) Branch")) ===
      Party("Australian Labor Party"))
  }

  "the ALP" should "not be in the Coalition" in {
    assert(PartyEquivalenceComputation.isInCoalition(Party("Australian Labor Party")) === false)
  }

  "the Liberal Party" should "be in the Coalition" in {
    assert(PartyEquivalenceComputation.isInCoalition(Party("Liberal Party of Australia")) === true)
  }

  "a party equivalent to the Liberal Party" should "be in the Coalition" in {
    assert(PartyEquivalenceComputation.isInCoalition(Party("Liberal")) === true)
  }

  "the Nationals" should "be in the coalition" in {
    assert(PartyEquivalenceComputation.isInCoalition(Party("The Nationals")) === true)
  }

  "a party whose national equivalent is the Liberal Party" should "be in the Coalition" in {
    assert(PartyEquivalenceComputation.isInCoalition(Party("Liberal National Party of Queensland")) === true)
  }

}
