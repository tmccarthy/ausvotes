package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.computations.BallotWithFacts
import au.id.tmm.ausvotes.core.tallies.TallyBundle.TraversableOps
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

  private val teir1Tallier: Tallier1[String] =
    TallierBuilder.counting(BallotCounter.FormalBallots).groupedBy(StringGrouper)

  private val teir2Tallier: Tallier2[String, Int] =
    TallierBuilder.counting(BallotCounter.FormalBallots).groupedBy(StringGrouper, IntGrouper)

  private object StringGrouper extends BallotGrouping[String] {
    override def groupsOf(ballotWithFacts: BallotWithFacts): Set[String] = throw new NotImplementedError()

    override def name: String = "string"
  }

  private object IntGrouper extends BallotGrouping[Int] {
    override def groupsOf(ballotWithFacts: BallotWithFacts): Set[Int] = throw new NotImplementedError()

    override def name: String = "int"
  }
}
