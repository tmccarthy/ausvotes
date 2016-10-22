package au.id.tmm.senatedb.computations.firstpreference

import au.id.tmm.senatedb.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.senatedb.fixtures.{Ballots, Candidates}
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.parsing.Party
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class FirstPreferenceCalculatorSpec extends ImprovedFlatSpec {

  private val state = State.ACT
  private val candidates = Candidates.ACT.candidates

  private val normaliser = BallotNormaliser(candidates)
  private val sut = FirstPreferenceCalculator(SenateElection.`2016`, state, candidates)

  import Ballots.ACT._
  import normaliser._

  "the first preference calculator" should "reject informal ballots" in {
    intercept[IllegalArgumentException] {
      sut.firstPreferencedPartyOf(normalise(btlMissedNumberBelow6))
    }
  }

  it should "get the party of the first preference when the ballot is atl" in {
    assert(sut.firstPreferencedPartyOf(normalise(oneAtl)) contains Party(SenateElection.`2016`, "Liberal Democratic Party"))
  }

  it should "get the party of the first preferenced candidate when the ballot is btl" in {
    assert(sut.firstPreferencedPartyOf(normalise(formalBtl)) contains Party(SenateElection.`2016`, "Liberal Democrats"))
  }

  it should "not have a first preference if the first preferenced candidate was independent" in {
    // Have to run this test in the NT cos all of the ACT candidates had parties
    val ntCandidates = Candidates.NT.candidates
    val ntNormaliser = BallotNormaliser(ntCandidates)

    val sut = FirstPreferenceCalculator(SenateElection.`2016`, State.NT, ntCandidates)

    assert(sut.firstPreferencedPartyOf(ntNormaliser.normalise(Ballots.NT.firstPreferenceUngroupedIndy)) === None)
  }
}
