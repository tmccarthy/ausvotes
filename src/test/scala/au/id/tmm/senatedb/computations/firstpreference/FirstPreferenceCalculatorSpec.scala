package au.id.tmm.senatedb.computations.firstpreference

import au.id.tmm.senatedb.computations.ballotnormalisation.BallotNormaliser
import au.id.tmm.senatedb.fixtures.{Ballots, Candidates}
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.computation.FirstPreference
import au.id.tmm.senatedb.model.parsing.{Party, Ungrouped}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class FirstPreferenceCalculatorSpec extends ImprovedFlatSpec {

  private val state = State.ACT
  private val candidates = Candidates.ACT.candidates

  private val normaliser = BallotNormaliser(SenateElection.`2016`, state, candidates)
  private val sut = FirstPreferenceCalculator(SenateElection.`2016`, state, candidates)

  import Ballots.ACT._
  import normaliser._

  "the first preference calculator" should "reject informal ballots" in {
    intercept[IllegalArgumentException] {
      sut.firstPreferenceOf(normalise(btlMissedNumberBelow6))
    }
  }

  it should "get the party of the first preference when the ballot is atl" in {
    assert(sut.firstPreferenceOf(normalise(oneAtl)).party contains Party(SenateElection.`2016`, "Liberal Democratic Party"))
  }

  it should "get the party of the first preferenced candidate when the ballot is btl" in {
    assert(sut.firstPreferenceOf(normalise(formalBtl)).party contains Party(SenateElection.`2016`, "Liberal Democrats"))
  }

  it should "not have a first preference if the first preferenced candidate was independent" in {
    // Have to run this test in the NT cos all of the ACT candidates had parties
    val ntCandidates = Candidates.NT.candidates
    val ntNormaliser = BallotNormaliser(SenateElection.`2016`, State.NT, ntCandidates)

    val sut = FirstPreferenceCalculator(SenateElection.`2016`, State.NT, ntCandidates)

    assert(sut.firstPreferenceOf(ntNormaliser.normalise(Ballots.NT.firstPreferenceUngroupedIndy)) === FirstPreference(Ungrouped, None))
  }
}
