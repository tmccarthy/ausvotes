package au.id.tmm.senatedb.core.savings

import au.id.tmm.senatedb.core.computations.savings.SavingsComputation
import au.id.tmm.senatedb.core.fixtures.{BallotFixture, TestsBallotFacts}
import au.id.tmm.senatedb.core.model.computation.SavingsProvision._
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class SavingsComputationSpec extends ImprovedFlatSpec with TestsBallotFacts {

  "the saved ballots computation" should "identify ballots that use ticks" in {
    val ballot = BallotFixture.ACT.tickedAtl
    val normalised = normalise(ballot)

    assert(SavingsComputation.savingsProvisionsUsedBy(ballot, normalised) === Set(UsedTick))
  }

  it should "identify ballots that use crosses" in {
    val ballot = BallotFixture.ACT.crossedAtl
    val normalised = normalise(ballot)

    assert(SavingsComputation.savingsProvisionsUsedBy(ballot, normalised) === Set(UsedCross))
  }

  it should "identify a ballot with a repeated number ATL" in {
    val ballot = BallotFixture.ACT.atlWithRepeatedNumbers
    val normalised = normalise(ballot)

    assert(SavingsComputation.savingsProvisionsUsedBy(ballot, normalised) contains CountingErrorAtl)
  }

  it should "identify a ballot with a repeated number BTL after 6" in {
    val ballot = BallotFixture.ACT.btlRepeatedNumberAfter6
    val normalised = normalise(ballot)

    assert(SavingsComputation.savingsProvisionsUsedBy(ballot, normalised) contains CountingErrorBtl)
  }

  it should "identify a ballot with a missed number ATL" in {
    val ballot = BallotFixture.ACT.atlMissedNumbers
    val normalised = normalise(ballot)

    assert(SavingsComputation.savingsProvisionsUsedBy(ballot, normalised) contains CountingErrorAtl)
  }

  it should "identify a ballot with a missed number BTL" in {
    val ballot = BallotFixture.ACT.btlMissedNumberAfter6
    val normalised = normalise(ballot)

    assert(SavingsComputation.savingsProvisionsUsedBy(ballot, normalised) contains CountingErrorBtl)
  }

  it should "identify a ballot with at least one but less than 6 formal preferences above the line" in {
    val ballot = BallotFixture.ACT.oneAtl
    val normalised = normalise(ballot)

    assert(SavingsComputation.savingsProvisionsUsedBy(ballot, normalised) contains InsufficientPreferencesAtl)
  }

  it should "identify a ballot with at least 6 but less than 12 formal preferences below the line" in {
    val ballot = BallotFixture.ACT.sixNumberedBtl
    val normalised = normalise(ballot)

    assert(SavingsComputation.savingsProvisionsUsedBy(ballot, normalised) contains InsufficientPreferencesBtl)
  }

  it should "not count a normal ballot atl" in {
    val ballot = BallotFixture.ACT.formalAtl
    val normalised = normalise(ballot)

    assert(SavingsComputation.savingsProvisionsUsedBy(ballot, normalised).isEmpty)
  }

  it should "not count a normal ballot btl" in {
    val ballot = BallotFixture.ACT.formalBtl
    val normalised = normalise(ballot)

    assert(SavingsComputation.savingsProvisionsUsedBy(ballot, normalised).isEmpty)
  }
}
