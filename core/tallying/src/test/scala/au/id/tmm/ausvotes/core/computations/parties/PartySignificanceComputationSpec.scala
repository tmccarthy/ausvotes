package au.id.tmm.ausvotes.core.computations.parties

import au.id.tmm.ausvotes.model.Party
import au.id.tmm.ausvotes.model.PartySignificance._
import org.scalatest.FlatSpec

class PartySignificanceComputationSpec extends FlatSpec {

  "party significance" should "be 'major' for the ALP" in {
    assert(PartySignificanceComputation.of(Some(Party("Australian Labor Party"))) === Major)
  }

  it should "be 'major' for a party whose national equivalent is the ALP" in {
    assert(PartySignificanceComputation.of(Some(Party("Australian Labor Party (Northern Territory) Branch"))) === Major)
  }

  it should "be 'major' for the Greens" in {
    assert(PartySignificanceComputation.of(Some(Party("The Greens"))) === Major)
  }

  it should "be 'major' for a party whose national equivalent is the Greens" in {
    assert(PartySignificanceComputation.of(Some(Party("The Greens (WA)"))) === Major)
  }

  it should "be 'major' for the Liberal Party of Australia" in {
    assert(PartySignificanceComputation.of(Some(Party("Liberal Party of Australia"))) === Major)
  }

  it should "be 'major' for any coalition party" in {
    assert(PartySignificanceComputation.of(Some(Party("The Nationals"))) === Major)
  }

  it should "be 'minor' for Family First" in {
    assert(PartySignificanceComputation.of(Some(Party("Family First Party"))) === Minor)
  }

  it should "be 'independent' for an independent" in {
    assert(PartySignificanceComputation.of(None) === Independent)
  }

}
