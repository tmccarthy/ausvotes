package au.id.tmm.senatedb.reporting.tally

import scala.collection.mutable

final case class DoublyTieredTally[A, B](values: Map[A, Tally[B]]) extends TallyLike {
  override type SelfType = DoublyTieredTally[A, B]

  override def +(that: DoublyTieredTally[A, B]): DoublyTieredTally[A, B] =
    this.mergeWith(that, key => this.values.getOrElse(key, Tally()) + that.values.getOrElse(key, Tally()))

  override def /(that: DoublyTieredTally[A, B]): DoublyTieredTally[A, B] =
    this.mergeWith(that, key => this.values.getOrElse(key, Tally()) / that.values(key))

  private def mergeWith(that: DoublyTieredTally[A, B], newValueForKey: A => Tally[B]): DoublyTieredTally[A, B] = {
    val newKeys = this.values.keySet ++ that.values.keySet

    val newEntries = newKeys.toStream
      .map(key => key -> newValueForKey(key))

    DoublyTieredTally(newEntries.toMap)
  }

  override def /(k: Double): DoublyTieredTally[A, B] = {
    if (k == 0) {
      throw new ArithmeticException()
    }

    DoublyTieredTally(values.mapValues(_ / k))
  }
}

object DoublyTieredTally {
  private val empty: DoublyTieredTally[Any, Any] = DoublyTieredTally(Map.empty[Any, Tally[Any]])

  def apply[A, B](): DoublyTieredTally[A, B] = empty.asInstanceOf[DoublyTieredTally[A, B]]

  def apply[A, B](entries: (A, Tally[B])*): DoublyTieredTally[A, B] = DoublyTieredTally(entries.toMap)

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

    def build(): DoublyTieredTally[A, B] = {
      val values = this.values.toMap.mapValues(v => Tally(v.toMap))

      DoublyTieredTally(values)
    }
  }
}