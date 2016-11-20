package au.id.tmm.senatedb.core.tallies

import au.id.tmm.senatedb.core.tallies.Tally.MapOps
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class TallySpec extends ImprovedFlatSpec {

  "a tally" can "be added" in {
    val left = Tally("A" -> 1, "B" -> 2)
    val right = Tally("A" -> 2, "B" -> 3)

    assert(left + right === Tally("A" -> 3, "B" -> 5))
  }

  it should "count missing keys as 0 when adding" in {
    val left = Tally("A" -> 1, "B" -> 2)
    val right = Tally("B" -> 3d)

    assert(left + right === Tally("A" -> 1, "B" -> 5))
  }

  it can "be built by incrementing keys by 1" in {
    val builder = Tally.Builder[String]()

    builder.increment("A")
    builder.increment("A")
    builder.increment("B")

    val tally = builder.build()

    assert(tally === Tally("A" -> 2, "B" -> 1))
  }

  it can "be built by incrementing keys by any amount" in {
    val builder = Tally.Builder[String]()

    builder.incrementBy("A", 2.5)
    builder.incrementBy("B", 1)

    val tally = builder.build()

    assert(tally === Tally("A" -> 2.5, "B" -> 1))
  }

  it can "be built directly from a map" in {
    val map: Map[String, Double] = Map("A" -> 1d)

    assert(Tally(map) === map.toTally)
  }

  it should "support result lookup directly" in {
    val tally = Tally("A" -> 2, "B" -> 8)

    assert(tally("A") === 2)
  }
}
