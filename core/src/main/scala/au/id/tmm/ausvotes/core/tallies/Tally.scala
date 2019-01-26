package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.model.Codecs
import au.id.tmm.ausvotes.model.Codecs.Codec
import cats.implicits._
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder, Json}

trait Tally {
  type SelfType <: Tally

  def +(that: SelfType): SelfType

  def /(denominator: Double): SelfType

  def /(denominator: SelfType): SelfType

  def accumulated: Tally0
}

object Tally {

  private[tallies] def generalTallyEncoder[T_TALLY, K: Encoder, V: Encoder](
                                                                             asKvPairs: T_TALLY => Iterable[(K, V)],
                                                                           ): Encoder[T_TALLY] =
    t => {
      asKvPairs(t)
        .map { case (key, value) =>
          Json.obj(
            "key" -> key.asJson,
            "value" -> value.asJson,
          )
        }.asJson
    }

  private[tallies] def generalTallyDecoder[T_TALLY, K: Decoder, V: Decoder](
                                                                             fromMap: Map[K, V] => T_TALLY,
                                                                           ): Decoder[T_TALLY] =
    c => c.as[List[Json]].flatMap { jsons =>
      jsons.traverse { json =>
        val c = json.hcursor

        for {
          key <- c.get[K]("key")
          value <- c.get[V]("value")
        } yield key -> value
      }.map { kvPairs =>
        fromMap(kvPairs.toMap)
      }
    }

}

// TODO this was a dumb idea. get rid of it
final case class Tally0(value: Double) extends Tally {
  override type SelfType = Tally0

  override def +(that: Tally0): Tally0 = Tally0(this.value + that.value)

  override def /(denominator: Double): Tally0 = {
    if (denominator == 0) {
      throw new ArithmeticException("Cannot divide by 0")
    } else {
      Tally0(this.value / denominator)
    }
  }

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

  implicit val encoder: Codec[Tally0] = Codecs.simpleCodec[Tally0, Double](_.value, Tally0.apply)
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

  def asStream: Stream[(T_GROUP_1, Tally0)] = asMap.toStream
    .map { case (key, tally0) =>
      key -> tally0
    }

  override def /(denominator: Tally1[T_GROUP_1]): Tally1[T_GROUP_1] = {
    val newMap = asMap
      .map { case (key, value) =>
        key -> value / denominator.asMap.getOrElse(key, throw new NoSuchElementException("key not found: " + key))
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

  implicit def encoder[T_GROUP_1: Encoder]: Encoder[Tally1[T_GROUP_1]] =
    Tally.generalTallyEncoder[Tally1[T_GROUP_1], T_GROUP_1, Tally0](_.asStream)

  implicit def decoder[T_GROUP_1: Decoder]: Decoder[Tally1[T_GROUP_1]] =
    Tally.generalTallyDecoder[Tally1[T_GROUP_1], T_GROUP_1, Tally0](m => Tally1.apply(m))

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

  def asStream: Stream[(T_GROUP_1, T_GROUP_2, Tally0)] = asMap.toStream
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

  implicit def encoder[T_GROUP_1: Encoder, T_GROUP_2: Encoder]: Encoder[Tally2[T_GROUP_1, T_GROUP_2]] =
    Tally.generalTallyEncoder[Tally2[T_GROUP_1, T_GROUP_2], T_GROUP_1, Tally1[T_GROUP_2]](_.asMap.toStream)

  implicit def decoder[T_GROUP_1: Decoder, T_GROUP_2: Decoder]: Decoder[Tally2[T_GROUP_1, T_GROUP_2]] =
    Tally.generalTallyDecoder[Tally2[T_GROUP_1, T_GROUP_2], T_GROUP_1, Tally1[T_GROUP_2]](m => Tally2.apply(m))

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

  def asStream: Stream[(T_GROUP_1, T_GROUP_2, T_GROUP_3, Tally0)] = asMap.toStream
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

  implicit def encoder[T_GROUP_1: Encoder, T_GROUP_2: Encoder, T_GROUP_3: Encoder]: Encoder[Tally3[T_GROUP_1, T_GROUP_2, T_GROUP_3]] =
    Tally.generalTallyEncoder[Tally3[T_GROUP_1, T_GROUP_2, T_GROUP_3], T_GROUP_1, Tally2[T_GROUP_2, T_GROUP_3]](_.asMap.toStream)

  implicit def decoder[T_GROUP_1: Decoder, T_GROUP_2: Decoder, T_GROUP_3: Decoder]: Decoder[Tally3[T_GROUP_1, T_GROUP_2, T_GROUP_3]] =
    Tally.generalTallyDecoder[Tally3[T_GROUP_1, T_GROUP_2, T_GROUP_3], T_GROUP_1, Tally2[T_GROUP_2, T_GROUP_3]](m => Tally3.apply(m))

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

  def asStream: Stream[(T_GROUP_1, T_GROUP_2, T_GROUP_3, T_GROUP_4, Tally0)] = asMap.toStream
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

  implicit def encoder[T_GROUP_1: Encoder, T_GROUP_2: Encoder, T_GROUP_3: Encoder, T_GROUP_4: Encoder]: Encoder[Tally4[T_GROUP_1, T_GROUP_2, T_GROUP_3, T_GROUP_4]] =
    Tally.generalTallyEncoder[Tally4[T_GROUP_1, T_GROUP_2, T_GROUP_3, T_GROUP_4], T_GROUP_1, Tally3[T_GROUP_2, T_GROUP_3, T_GROUP_4]](_.asMap.toStream)

  implicit def decoder[T_GROUP_1: Decoder, T_GROUP_2: Decoder, T_GROUP_3: Decoder, T_GROUP_4: Decoder]: Decoder[Tally4[T_GROUP_1, T_GROUP_2, T_GROUP_3, T_GROUP_4]] =
    Tally.generalTallyDecoder[Tally4[T_GROUP_1, T_GROUP_2, T_GROUP_3, T_GROUP_4], T_GROUP_1, Tally3[T_GROUP_2, T_GROUP_3, T_GROUP_4]](m => Tally4.apply(m))

}
