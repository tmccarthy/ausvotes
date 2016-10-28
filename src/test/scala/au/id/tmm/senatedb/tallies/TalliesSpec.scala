package au.id.tmm.senatedb.tallies

import au.id.tmm.senatedb.computations.BallotWithFacts
import au.id.tmm.senatedb.tallies.Tallies.TraversableOps
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class TalliesSpec extends ImprovedFlatSpec {

  "a group of tallies" should "support lookup by tallier" in {
    val tallies = Tallies(
      NormalTallier -> Tally(),
      TieredTallier -> DoublyTieredTally()
    )

    assert(tallies.tallyBy(NormalTallier) === Tally())
  }

  it should "reject incompatible tally/tallier combinations" in {
    intercept[IllegalArgumentException](Tallies(NormalTallier -> DoublyTieredTally()))
  }

  it can "be added to another group of tallies" in {
    val left = Tallies(NormalTallier -> Tally("A" -> 1d))
    val right = Tallies(NormalTallier -> Tally("A" -> 3d))

    assert(left + right === Tallies(NormalTallier -> Tally("A" -> 4d)))
  }

  it should "consider missing talliers as having an empty tally" in {
    val left = Tallies(NormalTallier -> Tally())
    val right = Tallies(NormalTallier -> Tally("A" -> 3d))

    assert(left + right === Tallies(NormalTallier -> Tally("A" -> 3d)))
  }

  it can "be built directly from a map" in {
    assert(Map(NormalTallier -> Tally()).toTallies === Tallies(NormalTallier -> Tally()))
  }

  private object NormalTallier extends Tallier.NormalTallier[String] {
    override def tally(ballotsWithFacts: Vector[BallotWithFacts]): Tally[String] = Tally()
  }

  private object TieredTallier extends Tallier.TieredTallier[String, Int] {
    override def tally(ballotsWithFacts: Vector[BallotWithFacts]): DoublyTieredTally[String, Int] = DoublyTieredTally()
  }
}
