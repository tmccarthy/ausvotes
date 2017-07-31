package au.id.tmm.senatedb.api.persistence.entities.stats

import au.id.tmm.senatedb.core.tallies.Tally0

// Zero indexed!
final case class Rank(ordinal: Int, totalCount: Int)

object Rank {

  def ranksFrom[A](values: Map[A, Double]): Map[A, Rank] = {
    values
      .toStream
      .sortBy(_._2)
      .reverse
      .zipWithIndex
      .map { case ((key, value), ordinal) =>
          key -> Rank(ordinal, values.size)
      }
      .toMap
  }

  def ranksFromTallies[A](values: Map[A, Tally0]): Map[A, Rank] = {
    val valuesWithResolvedTallies = values.map { case (key, tally) =>
      key -> tally.value
    }

    ranksFrom(valuesWithResolvedTallies)
  }

}
