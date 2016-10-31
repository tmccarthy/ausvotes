package au.id.tmm.senatedb.tallies

import scala.collection.mutable

final case class TieredTally[A, B](values: Map[A, Tally[B]]) extends TallyLike {
  override type SelfType = TieredTally[A, B]

  override def +(that: TieredTally[A, B]): TieredTally[A, B] =
    this.mergeWith(that, key => this.values.getOrElse(key, Tally()) + that.values.getOrElse(key, Tally()))

  override def /(that: TieredTally[A, B]): TieredTally[A, B] =
    this.mergeWith(that, key => this.values.getOrElse(key, Tally()) / that.values(key))

  private def mergeWith(that: TieredTally[A, B], newValueForKey: A => Tally[B]): TieredTally[A, B] = {
    val newKeys = this.values.keySet ++ that.values.keySet

    val newEntries = newKeys.toStream
      .map(key => key -> newValueForKey(key))

    TieredTally(newEntries.toMap)
  }

  override def /(k: Double): TieredTally[A, B] = {
    if (k == 0) {
      throw new ArithmeticException()
    }

    TieredTally(values.mapValues(_ / k))
  }

  def apply(key: A): Tally[B] = values.getOrElse(key, Tally())
}

object TieredTally {
  private val empty: TieredTally[Any, Any] = TieredTally(Map.empty[Any, Tally[Any]])

  def apply[A, B](): TieredTally[A, B] = empty.asInstanceOf[TieredTally[A, B]]

  def apply[A, B](entries: (A, Tally[B])*): TieredTally[A, B] = TieredTally(entries.toMap)

  object Builder {
    def apply[A, B](): Builder[A, B] = new Builder[A, B]()
  }

  final class Builder[A, B] private () {
    val values: mutable.Map[A, mutable.Map[B, Double]] = mutable.Map[A, mutable.Map[B, Double]]()

    def increment(keyA: A, keyB: B): Unit = incrementBy(keyA, keyB, 1)

    def incrementBy(keyA: A, keyB: B, amount: Double): Unit = {
      val mapForKey = values.getOrElse(keyA, mutable.Map())

      val newValue = mapForKey.getOrElse(keyB, 0d) + amount

      mapForKey.put(keyB, newValue)

      values.put(keyA, mapForKey)
    }

    def build(): TieredTally[A, B] = {
      val values = this.values.toMap.mapValues(v => Tally(v.toMap))

      TieredTally(values)
    }
  }
}