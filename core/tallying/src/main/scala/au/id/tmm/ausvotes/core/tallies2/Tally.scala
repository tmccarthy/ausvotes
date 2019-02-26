package au.id.tmm.ausvotes.core.tallies2

import cats.Monoid

final case class Tally[G, A : Monoid](asMap: Map[G, A]) {

  private def emptyVal = Monoid[A].empty
  private def sum(left: A, right: A) = Monoid[A].combine(left, right)

  def + (that: Tally[G, A]): Tally[G, A] = {
    val newKeys = this.asMap.keySet ++ that.asMap.keySet

    val newUnderlyingMap = newKeys
      .toStream
      .map { key =>
        val thisValue = this.asMap.getOrElse(key, emptyVal)
        val thatValue = that.asMap.getOrElse(key, emptyVal)
        key -> sum(thisValue, thatValue)
      }
      .toMap

    Tally(newUnderlyingMap)
  }

}

object Tally {

  def empty[G, A : Monoid]: Tally[G, A] = Tally[G, A](Map.empty[G, A])

  implicit def tallyIsAMonoid[G, A : Monoid]: Monoid[Tally[G, A]] = new Monoid[Tally[G, A]] {
    override def empty: Tally[G, A] = Tally.empty

    override def combine(left: Tally[G, A], right: Tally[G, A]): Tally[G, A] = left + right
  }

}
