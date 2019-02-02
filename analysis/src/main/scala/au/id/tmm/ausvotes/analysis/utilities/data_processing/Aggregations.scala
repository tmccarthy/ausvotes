package au.id.tmm.ausvotes.analysis.utilities.data_processing

import cats.kernel.Monoid

object Aggregations {

  def groupByAndAggregate[X, G, A : Monoid](elements: Iterable[X], groupBy: X => G, aggregatable: X => A): Map[G, A] = {
    elements
      .groupBy(groupBy)
      .map { case (g, xs) => g -> implicitly[Monoid[A]].combineAll(xs.map(aggregatable)) }
  }

  implicit class AggregationOps[X](iterable: Iterable[X]) {
    def groupByAndAggregate[G, A : Monoid](groupBy: X => G)(aggregatable: X => A): Map[G, A] =
      Aggregations.groupByAndAggregate(iterable, groupBy, aggregatable)
  }

}
