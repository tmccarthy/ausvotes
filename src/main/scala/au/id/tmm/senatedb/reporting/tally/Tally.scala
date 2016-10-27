package au.id.tmm.senatedb.reporting.tally

import scala.collection.mutable

final case class Tally[A](values: Map[A, Double]) extends TallyLike {
  override type SelfType = Tally[A]

  override def +(that: Tally[A]): Tally[A] =
    this.mergeWith(that, key => this.values.getOrElse(key, 0d) + that.values.getOrElse(key, 0d))

  override def /(that: Tally[A]): Tally[A] =
    this.mergeWith(that, key => this.values.getOrElse(key, 0d) / that.values(key))

  private def mergeWith(that: Tally[A], newValueForKey: A => Double): Tally[A] = {
    val newKeys = this.values.keySet ++ that.values.keySet

    val newEntries = newKeys.toStream
      .map(key => key -> newValueForKey(key))

    Tally(newEntries.toMap)
  }

  override def /(k: Double): Tally[A] = {
    if (k == 0) {
      throw new ArithmeticException()
    }

    Tally(values.mapValues(_ / k))
  }
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