package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.tallies.TallyBundle.TraversableOps
import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class TallyBundleSpec extends ImprovedFlatSpec {

  "a group of tallies" should "support lookup by tallier" in {
    val tallies = TallyBundle(
      teir1Tallier -> Tally1(),
      teir2Tallier -> Tally2()
    )

    assert(tallies.tallyProducedBy(teir1Tallier) === Tally1())
  }

  it should "reject incompatible tally/tallier combinations" in {
    intercept[IllegalArgumentException](TallyBundle(teir1Tallier -> Tally2()))
  }

  it can "be added to another group of tallies" in {
    val left = TallyBundle(teir1Tallier -> Tally1("A" -> 1d))
    val right = TallyBundle(teir1Tallier -> Tally1("A" -> 3d))

    assert(left + right === TallyBundle(teir1Tallier -> Tally1("A" -> 4d)))
  }

  it should "consider missing talliers as having an empty tally" in {
    val left = TallyBundle(teir1Tallier -> Tally1())
    val right = TallyBundle(teir1Tallier -> Tally1("A" -> 3d))

    assert(left + right === TallyBundle(teir1Tallier -> Tally1("A" -> 3d)))
  }

  it can "be built directly from a map" in {
    val actual = Map(teir1Tallier -> Tally1()).toTallyBundle
    val expected = TallyBundle(teir1Tallier -> Tally1())

    assert(actual === expected)
  }

  private val teir1Tallier: Tallier1[SenateElection] =
    TallierBuilder.counting(BallotCounter.FormalBallots).groupedBy(BallotGrouping.SenateElection)

  private val teir2Tallier: Tallier2[SenateElection, State] =
    TallierBuilder.counting(BallotCounter.FormalBallots).groupedBy(BallotGrouping.SenateElection, BallotGrouping.State)

}
