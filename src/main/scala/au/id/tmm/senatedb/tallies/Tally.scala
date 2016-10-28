package au.id.tmm.senatedb.tallies

import au.id.tmm.utilities.collection.CollectionUtils.DoubleMapOps

import scala.collection.mutable

final case class Tally[A](values: Map[A, Double]) extends TallyLike {
  override type SelfType = Tally[A]

  override def +(that: Tally[A]): Tally[A] = Tally(this.values + that.values)

  override def /(that: Tally[A]): Tally[A] = Tally(this.values / that.values)

  override def /(k: Double): Tally[A] = Tally(values / k)

}

object Tally {
  private val empty: Tally[Any] = Tally(Map.empty[Any, Double])

  def apply[A](): Tally[A] = empty.asInstanceOf[Tally[A]]

  def apply[A](entries: (A, Double)*): Tally[A] = Tally(entries.toMap)

  object Builder {
    def apply[A](): Builder[A] = new Builder[A]()
  }

  final class Builder[A] private () {
    val values: mutable.Map[A, Double] = mutable.Map[A, Double]()

    def increment(key: A): Unit = incrementBy(key, 1)

    def incrementBy(key: A, amount: Double): Unit = {
      val newValue = values.getOrElse(key, 0d) + amount

      values.put(key, newValue)
    }

    def build(): Tally[A] = Tally(values.toMap)
  }
}