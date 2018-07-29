package au.id.tmm.ausvotes.core.computations.firstpreference

import au.id.tmm.ausvotes.core.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.ausvotes.core.fixtures.{BallotFixture, CandidateFixture}
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.computation.FirstPreference
import au.id.tmm.ausvotes.core.model.parsing.Party.{Independent, RegisteredParty}
import au.id.tmm.ausvotes.core.model.parsing.Ungrouped
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class FirstPreferenceCalculatorSpec extends ImprovedFlatSpec {

  private val state = State.ACT
  private val candidates = CandidateFixture.ACT.candidates

  private val normaliser = BallotNormaliser(SenateElection.`2016`, state, candidates)
  private val sut = FirstPreferenceCalculator(SenateElection.`2016`, state, candidates)

  import BallotFixture.ACT._
  import normaliser._

  "the first preference calculator" should "reject informal ballots" in {
    intercept[IllegalArgumentException] {
      sut.firstPreferenceOf(normalise(btlMissedNumberBelow6))
    }
  }

  it should "get the party of the first preference when the ballot is atl" in {
    assert(sut.firstPreferenceOf(normalise(oneAtl)).party === RegisteredParty("Liberal Democratic Party"))
  }

  it should "get the party of the first preferenced candidate when the ballot is btl" in {
    assert(sut.firstPreferenceOf(normalise(formalBtl)).party === RegisteredParty("Liberal Democrats"))
  }

  it should "not have a first preference if the first preferenced candidate was independent" in {
    // Have to run this test in the NT cos all of the ACT candidates had parties
    val ntCandidates = CandidateFixture.NT.candidates
    val ntNormaliser = BallotNormaliser(SenateElection.`2016`, State.NT, ntCandidates)

    val sut = FirstPreferenceCalculator(SenateElection.`2016`, State.NT, ntCandidates)

    assert(sut.firstPreferenceOf(ntNormaliser.normalise(BallotFixture.NT.firstPreferenceUngroupedIndy)) ===
      FirstPreference(Ungrouped(SenateElection.`2016`, State.NT), Independent))
  }

  it should "correctly get the first preference when an ungrouped candidate that is a member of a party is preferenced btl" in {
    assert(sut.firstPreferenceOf(normalise(btlFirstPrefUngrouped)) ===
      FirstPreference(Ungrouped(SenateElection.`2016`, State.ACT), RegisteredParty("Mature Australia")))
  }
}
