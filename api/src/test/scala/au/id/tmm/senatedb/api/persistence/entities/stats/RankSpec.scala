package au.id.tmm.senatedb.api.persistence.entities.stats

import au.id.tmm.senatedb.core.tallies.Tally0
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class RankSpec extends ImprovedFlatSpec {

  "a rank" should "have an ordinal" in {
    val sut = Rank(ordinal = 0, totalCount = 1)

    assert(sut.ordinal === 0)
  }

  it should "have a total count" in {
    val sut = Rank(ordinal = 0, totalCount = 1)

    assert(sut.totalCount === 1)
  }

  "ranks" can "be generated from a tally" in {
    val tally = Map(
      "A" -> Tally0(2),
      "B" -> Tally0(4),
      "C" -> Tally0(6),
    )

    val actualRanks = Rank.ranksFromTallies(tally)
    val expectedRanks = Map(
      "C" -> Rank(ordinal = 0, totalCount = 3),
      "B" -> Rank(ordinal = 1, totalCount = 3),
      "A" -> Rank(ordinal = 2, totalCount = 3),
    )

    assert(expectedRanks === actualRanks)
  }
}
