package au.id.tmm.senatedb.core.tallies

trait Tally {
  type SelfType <: Tally

  def +(that: SelfType): SelfType

  def /(denominator: Double): SelfType

  def /(denominator: SelfType): SelfType

  def accumulated: Tally0
}

// TODO this was a dumb idea. get rid of it
final case class Tally0(value: Double) extends Tally {
  override type SelfType = Tally0

  override def +(that: Tally0): Tally0 = Tally0(this.value + that.value)

  override def /(denominator: Double): Tally0 = Tally0(this.value / denominator)

  override def /(denominator: Tally0): Tally0 = Tally0(this.value / denominator.value)

  override def accumulated: Tally0 = this
}

object Tally0 {
  def apply(): Tally0 = Tally0(0d)

  private[tallies] def sum(tallies: Traversable[Tally0]): Tally0 = Tally0 {
    tallies
      .map(_.value)
      .sum
  }
}

final case class Tally1[T_GROUP_1](asMap: Map[T_GROUP_1, Tally0]) extends Tally {
  override type SelfType = Tally1[T_GROUP_1]

  override def +(that: Tally1[T_GROUP_1]): Tally1[T_GROUP_1] = {
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

  override lazy val accumulated: Tally0 = Tally0.sum(asMap.values)

  def asStream: Stream[(T_GROUP_1, Double)] = asMap.toStream
    .map { case (key, tally0) =>
      key -> tally0.value
    }

  override def /(denominator: Tally1[T_GROUP_1]): Tally1[T_GROUP_1] = {
    val newMap = asMap
      .map { case (key, value) =>
        key -> value / denominator(key)
      }

    Tally1(newMap)
  }
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

  override lazy val accumulated: Tally0 = Tally0.sum(asMap.values.map(_.accumulated))

  def asStream: Stream[(T_GROUP_1, T_GROUP_2, Double)] = asMap.toStream
    .flatMap { case (key1, tally1) =>
      tally1.asStream
        .map { case (key2, amount) =>
          (key1, key2, amount)
        }
    }

  override def /(denominator: Tally2[T_GROUP_1, T_GROUP_2]): Tally2[T_GROUP_1, T_GROUP_2] = {
    val newMap = asMap
      .map { case (key, value) =>
        key -> value / denominator(key)
      }

    Tally2(newMap)
  }
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

  override lazy val accumulated: Tally0 = Tally0.sum(asMap.values.map(_.accumulated))

  def asStream: Stream[(T_GROUP_1, T_GROUP_2, T_GROUP_3, Double)] = asMap.toStream
    .flatMap { case (key1, tally1) =>
      tally1.asStream
        .map { case (key2, key3, amount) =>
          (key1, key2, key3, amount)
        }
    }

  override def /(denominator: Tally3[T_GROUP_1, T_GROUP_2, T_GROUP_3]): Tally3[T_GROUP_1, T_GROUP_2, T_GROUP_3] = {
    val newMap = asMap
      .map { case (key, value) =>
        key -> value / denominator(key)
      }

    Tally3(newMap)
  }
}

object Tally3 {
  def apply[T_GROUP_1, T_GROUP_2, T_GROUP_3](entries: (T_GROUP_1, Tally2[T_GROUP_2, T_GROUP_3])*): Tally3[T_GROUP_1, T_GROUP_2, T_GROUP_3] = Tally3(
    entries.toMap
  )
}

final case class Tally4[T_GROUP_1, T_GROUP_2, T_GROUP_3, T_GROUP_4](asMap: Map[T_GROUP_1, Tally3[T_GROUP_2, T_GROUP_3, T_GROUP_4]]) extends Tally {
  override type SelfType = Tally4[T_GROUP_1, T_GROUP_2, T_GROUP_3, T_GROUP_4]

  override def +(that: Tally4[T_GROUP_1, T_GROUP_2, T_GROUP_3, T_GROUP_4]): Tally4[T_GROUP_1, T_GROUP_2, T_GROUP_3, T_GROUP_4] = {
    val newKeys = this.asMap.keySet ++ that.asMap.keySet

    val newUnderlyingMap = newKeys
      .toStream
      .map { key =>
        val thisValue = this.asMap.getOrElse(key, Tally3())
        val thatValue = that.asMap.getOrElse(key, Tally3())
        key -> (thisValue + thatValue)
      }
      .toMap

    Tally4(newUnderlyingMap)
  }

  override def /(denominator: Double): Tally4[T_GROUP_1, T_GROUP_2, T_GROUP_3, T_GROUP_4] = Tally4 {
    asMap.mapValues(_ / denominator)
  }

  def apply(key: T_GROUP_1): Tally3[T_GROUP_2, T_GROUP_3, T_GROUP_4] = asMap.getOrElse(key, Tally3())

  override lazy val accumulated: Tally0 = Tally0.sum(asMap.values.map(_.accumulated))

  def asStream: Stream[(T_GROUP_1, T_GROUP_2, T_GROUP_3, T_GROUP_4, Double)] = asMap.toStream
    .flatMap { case (key1, tally1) =>
      tally1.asStream
        .map { case (key2, key3, key4, amount) =>
          (key1, key2, key3, key4, amount)
        }
    }

  override def /(denominator: Tally4[T_GROUP_1, T_GROUP_2, T_GROUP_3, T_GROUP_4]): Tally4[T_GROUP_1, T_GROUP_2, T_GROUP_3, T_GROUP_4] = {
    val newMap = asMap
      .map { case (key, value) =>
        key -> value / denominator(key)
      }

    Tally4(newMap)
  }
}

object Tally4 {
  def apply[T_GROUP_1, T_GROUP_2, T_GROUP_3, T_GROUP_4](entries: (T_GROUP_1, Tally3[T_GROUP_2, T_GROUP_3, T_GROUP_4])*): Tally4[T_GROUP_1, T_GROUP_2, T_GROUP_3, T_GROUP_4] = Tally4(
    entries.toMap
  )
}