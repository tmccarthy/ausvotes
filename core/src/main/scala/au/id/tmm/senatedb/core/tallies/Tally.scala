package au.id.tmm.senatedb.core.tallies

trait Tally {
  type SelfType <: Tally

  def +(that: SelfType): SelfType

  def /(denominator: Double): SelfType
}

final case class Tally0(value: Double) extends Tally {
  override type SelfType = Tally0

  def +(that: Tally0): Tally0 = Tally0(this.value + that.value)

  override def /(denominator: Double): Tally0 = Tally0(this.value / denominator)
}

object Tally0 {
  def apply(): Tally0 = Tally0(0d)
}

final case class Tally1[T_GROUP_1](asMap: Map[T_GROUP_1, Tally0]) extends Tally {
  override type SelfType = Tally1[T_GROUP_1]

  def +(that: Tally1[T_GROUP_1]): Tally1[T_GROUP_1] = {
    val newKeys = this.asMap.keySet ++ that.asMap.keySet

    val newUnderlyingMap = newKeys
      .toStream
      .map { key =>
        val thisValue = this.asMap.getOrElse(key, Tally0())
        val thatValue = that.asMap.getOrElse(key, Tally0())
        key -> (thisValue + thatValue)
      }
      .toMap

    Tally1(newUnderlyingMap)
  }

  override def /(denominator: Double): Tally1[T_GROUP_1] = Tally1 {
    asMap
      .mapValues(_ / denominator)
  }

  def apply(key: T_GROUP_1): Tally0 = asMap.getOrElse(key, Tally0())
}

object Tally1 {
  def apply[T_GROUP_1](entries: (T_GROUP_1, Double)*): Tally1[T_GROUP_1] = Tally1(
    entries
      .toStream
      .map { case (group, rawWeight) =>
        group -> Tally0(rawWeight)
      }
      .toMap
  )
}

final case class Tally2[T_GROUP_1, T_GROUP_2](asMap: Map[T_GROUP_1, Tally1[T_GROUP_2]]) extends Tally {
  override type SelfType = Tally2[T_GROUP_1, T_GROUP_2]

  override def +(that: Tally2[T_GROUP_1, T_GROUP_2]): Tally2[T_GROUP_1, T_GROUP_2] = {
    val newKeys = this.asMap.keySet ++ that.asMap.keySet

    val newUnderlyingMap = newKeys
      .toStream
      .map { key =>
        val thisValue = this.asMap.getOrElse(key, Tally1())
        val thatValue = that.asMap.getOrElse(key, Tally1())
        key -> (thisValue + thatValue)
      }
      .toMap

    Tally2(newUnderlyingMap)
  }

  override def /(denominator: Double): Tally2[T_GROUP_1, T_GROUP_2] = Tally2 {
    asMap
      .mapValues(_ / denominator)
  }

  def apply(key: T_GROUP_1): Tally1[T_GROUP_2] = asMap.getOrElse(key, Tally1())
}

object Tally2 {
  def apply[T_GROUP_1, T_GROUP_2](entries: (T_GROUP_1, Tally1[T_GROUP_2])*): Tally2[T_GROUP_1, T_GROUP_2] = Tally2(
    entries.toMap
  )
}

final case class Tally3[T_GROUP_1, T_GROUP_2, T_GROUP_3](asMap: Map[T_GROUP_1, Tally2[T_GROUP_2, T_GROUP_3]]) extends Tally {
  override type SelfType = Tally3[T_GROUP_1, T_GROUP_2, T_GROUP_3]

  override def +(that: Tally3[T_GROUP_1, T_GROUP_2, T_GROUP_3]): Tally3[T_GROUP_1, T_GROUP_2, T_GROUP_3] = {
    val newKeys = this.asMap.keySet ++ that.asMap.keySet

    val newUnderlyingMap = newKeys
      .toStream
      .map { key =>
        val thisValue = this.asMap.getOrElse(key, Tally2())
        val thatValue = that.asMap.getOrElse(key, Tally2())
        key -> (thisValue + thatValue)
      }
      .toMap

    Tally3(newUnderlyingMap)
  }

  override def /(denominator: Double): Tally3[T_GROUP_1, T_GROUP_2, T_GROUP_3] = Tally3 {
    asMap
      .mapValues(_ / denominator)
  }

  def apply(key: T_GROUP_1): Tally2[T_GROUP_2, T_GROUP_3] = asMap.getOrElse(key, Tally2())
}

object Tally3 {
  def apply[T_GROUP_1, T_GROUP_2, T_GROUP_3](entries: (T_GROUP_1, Tally2[T_GROUP_2, T_GROUP_3])*): Tally3[T_GROUP_1, T_GROUP_2, T_GROUP_3] = Tally3(
    entries.toMap
  )
}