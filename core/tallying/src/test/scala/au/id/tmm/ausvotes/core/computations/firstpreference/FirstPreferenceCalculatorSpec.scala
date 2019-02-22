package au.id.tmm.ausvotes.core.computations.firstpreference

import au.id.tmm.ausvotes.core.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.ausvotes.core.fixtures.{BallotFixture, CandidateFixture}
import au.id.tmm.ausvotes.model.Party
import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import au.id.tmm.ausvotes.model.stv.{FirstPreference, Ungrouped}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class FirstPreferenceCalculatorSpec extends ImprovedFlatSpec {

  private val election = SenateElection.`2016`.electionForState(State.ACT).get
  private val candidates = CandidateFixture.ACT.candidates

  private val normaliser = BallotNormaliser.forSenate(election, candidates)

  import BallotFixture.ACT._
  import normaliser._

  "the first preference calculator" should "reject informal ballots" in {
    intercept[IllegalStateException] {
      FirstPreferenceCalculator.firstPreferenceOf(normalise(btlMissedNumberBelow6))
    }
  }

  it should "get the party of the first preference when the ballot is atl" in {
    assert(FirstPreferenceCalculator.firstPreferenceOf(normalise(oneAtl)).party === Some(Party("Liberal Democratic Party")))
  }

  it should "get the party of the first preferenced candidate when the ballot is btl" in {
    assert(FirstPreferenceCalculator.firstPreferenceOf(normalise(formalBtl)).party === Some(Party("Liberal Democrats")))
  }

  it should "not have a first preference if the first preferenced candidate was independent" in {
    // Have to run this test in the NT cos all of the ACT candidates had parties
    val ntCandidates = CandidateFixture.NT.candidates
    val election = SenateElection.`2016`.electionForState(State.NT).get
    val ntNormaliser = BallotNormaliser.forSenate(election, ntCandidates)

    assert(FirstPreferenceCalculator.firstPreferenceOf(ntNormaliser.normalise(BallotFixture.NT.firstPreferenceUngroupedIndy)) ===
      FirstPreference(Ungrouped(election), None))
  }

  it should "correctly get the first preference when an ungrouped candidate that is a member of a party is preferenced btl" in {
    assert(FirstPreferenceCalculator.firstPreferenceOf(normalise(btlFirstPrefUngrouped)) ===
      FirstPreference(Ungrouped(election), Some(Party("Mature Australia"))))
  }
}
