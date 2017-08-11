package au.id.tmm.senatedb.api.persistence.daos

import au.id.tmm.senatedb.api.persistence.daos.ResultSetMocking.mockWrappedResultSets
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class StatRowConversionsTest extends ImprovedFlatSpec {

  private val columnNames = Vector(
    "stat.id",

    "stat.stat_class",

    "stat.election",
    "stat.state",
    "stat.division",
    "stat.vote_collection_point",

    "stat.amount",
    "stat.per_capita",

    "rank.id",

    "rank.stat",

    "rank.jurisdiction_level",

    "rank.ordinal",
    "rank.ordinal_per_capita",

    "rank.total_count",
  )

  "stat row conversions" should "convert stat rows" in {
    val rows = mockWrappedResultSets(columnNames)(
      (1, "formal_ballots", "2016", "SA", "MAKIN")
    )
  }

}
