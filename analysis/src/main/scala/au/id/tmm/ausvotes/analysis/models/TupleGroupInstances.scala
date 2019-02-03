package au.id.tmm.ausvotes.analysis.models

import cats.kernel.Monoid

object TupleGroupInstances {

  implicit def tuple2isAGroup[A1 : Monoid, A2 : Monoid]: Monoid[(A1, A2)] = new Monoid[(A1, A2)] {
    private val a1Instance: Monoid[A1] = Monoid[A1]
    private val a2Instance: Monoid[A2] = Monoid[A2]

    override def empty: (A1, A2) = (a1Instance.empty, a2Instance.empty)

    override def combine(x: (A1, A2), y: (A1, A2)): (A1, A2) = (a1Instance.combine(x._1, y._1), a2Instance.combine(x._2, y._2))
  }

}
