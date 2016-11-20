package au.id.tmm.senatedb.core.model

import au.id.tmm.senatedb.core.model.PartySignificance.{MajorParty, MinorParty}
import au.id.tmm.senatedb.core.model.parsing.Party
import au.id.tmm.senatedb.core.model.parsing.Party.RegisteredParty
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class PartySignificanceSpec extends ImprovedFlatSpec {

  "party significance" should "be 'major' for the ALP" in {
    assert(PartySignificance.of(RegisteredParty("Australian Labor Party")) === MajorParty)
  }

  it should "be 'major' for a party whose national equivalent is the ALP" in {
    assert(PartySignificance.of(RegisteredParty("Australian Labor Party (Northern Territory) Branch")) === MajorParty)
  }

  it should "be 'major' for the Greens" in {
    assert(PartySignificance.of(RegisteredParty("The Greens")) === MajorParty)
  }

  it should "be 'major' for a party whose national equivalent is the Greens" in {
    assert(PartySignificance.of(RegisteredParty("The Greens (WA)")) === MajorParty)
  }

  it should "be 'major' for the Liberal Party of Australia" in {
    assert(PartySignificance.of(RegisteredParty("Liberal Party of Australia")) === MajorParty)
  }

  it should "be 'major' for any coalition party" in {
    assert(PartySignificance.of(RegisteredParty("The Nationals")) === MajorParty)
  }

  it should "be 'minor' for Family First" in {
    assert(PartySignificance.of(RegisteredParty("Family First Party")) === MinorParty)
  }

  it should "be 'independent' for an independent" in {
    assert(PartySignificance.of(Party.Independent) === PartySignificance.Independent)
  }
}
