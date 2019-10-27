package au.id.tmm.ausvotes.core.tallies

import cats.Monoid
import cats.instances.either.catsStdInstancesForEither
import cats.instances.vector.catsStdInstancesForVector
import cats.syntax.traverse._
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder, Json}

final case class Tally[G, A : Monoid](asMap: Map[G, A]) extends PartialFunction[G, A] {

  private def emptyVal = Monoid[A].empty
  private def sum(left: A, right: A) = Monoid[A].combine(left, right)

  def + (that: Tally[G, A]): Tally[G, A] = {
    val newKeys = this.asMap.keySet ++ that.asMap.keySet

    val newUnderlyingMap = newKeys
      .map { key =>
        val thisValue = this.asMap.getOrElse(key, emptyVal)
        val thatValue = that.asMap.getOrElse(key, emptyVal)
        key -> sum(thisValue, thatValue)
      }
      .toMap

    Tally(newUnderlyingMap)
  }

  override def isDefinedAt(g: G): Boolean = asMap.isDefinedAt(g)

  override def apply(g: G): A = asMap.apply(g)

}

object Tally {

  def apply[G, A : Monoid](talliesPerGroup: (G, A)*): Tally[G, A] = new Tally(talliesPerGroup.toMap)

  def empty[G, A : Monoid]: Tally[G, A] = Tally[G, A](Map.empty[G, A])

  implicit def tallyIsAMonoid[G, A : Monoid]: Monoid[Tally[G, A]] = new Monoid[Tally[G, A]] {
    override def empty: Tally[G, A] = Tally.empty

    override def combine(left: Tally[G, A], right: Tally[G, A]): Tally[G, A] = left + right
  }

  implicit def decoder[G : Decoder, A : Decoder : Monoid]: Decoder[Tally[G, A]] = c =>
    for {
      jsonElements <- c.as[Vector[Json]]
      tuples <- jsonElements.traverse { json =>
        val c = json.hcursor

        for {
          key <- c.get[G]("key")
          value <- c.get[A]("value")
        } yield key -> value
      }
    } yield Tally(tuples.toMap)

  implicit def encoder[G : Encoder, A : Encoder]: Encoder[Tally[G, A]] = t =>
    Json.arr(
      t.asMap.map { case (group, value) => Json.obj("key" -> group.asJson, "value" -> value.asJson) }.toSeq: _*
    )

  trait UnNestedImplicits {
    implicit class UnNestedOps[G, A](tally: Tally[G, A]) {
      def toVector: Vector[(G, A)] = tally.asMap.toVector
    }
  }

  trait OnceNestedImplicits extends UnNestedImplicits {
    implicit class OnceNestedOps[G1, G2, A](tally: Tally[G1, Tally[G2, A]]) {
      def toVector: Vector[(G1, G2, A)] = {
        val builder = Vector.newBuilder[(G1, G2, A)]

        for {
          (group1, tier1) <- tally.asMap
          (group2, count) <- tier1.asMap
        } {
          builder += ((group1, group2, count))
        }

        builder.result()
      }
    }
  }

  trait TwiceNestedImplicits extends OnceNestedImplicits {
    implicit class TwiceNestedOps[G1, G2, G3, A](tally: Tally[G1, Tally[G2, Tally[G3, A]]]) {
      def toVector: Vector[(G1, G2, G3, A)] = {
        val builder = Vector.newBuilder[(G1, G2, G3, A)]

        for {
          (group1, tier1) <- tally.asMap
          (group2, tier2) <- tier1.asMap
          (group3, count) <- tier2.asMap
        } {
          builder += ((group1, group2, group3, count))
        }

        builder.result()
      }
    }
  }

  trait ThriceNestedImplicits extends TwiceNestedImplicits {
    implicit class ThriceNestedOps[G1, G2, G3, G4, A](tally: Tally[G1, Tally[G2, Tally[G3, Tally[G4, A]]]]) {
      def toVector: Vector[(G1, G2, G3, G4, A)] = {
        val builder = Vector.newBuilder[(G1, G2, G3, G4, A)]

        for {
          (group1, tier1) <- tally.asMap
          (group2, tier2) <- tier1.asMap
          (group3, tier3) <- tier2.asMap
          (group4, count) <- tier3.asMap
        } {
          builder += ((group1, group2, group3, group4, count))
        }

        builder.result()
      }
    }
  }

  object Ops extends ThriceNestedImplicits

}
