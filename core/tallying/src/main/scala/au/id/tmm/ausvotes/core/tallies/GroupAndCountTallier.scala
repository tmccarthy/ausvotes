package au.id.tmm.ausvotes.core.tallies

import cats.Monoid
import io.circe.{Decoder, Encoder}

import scala.collection.mutable

final case class GroupAndCountTallier[+T_GROUPER <: Grouper[G, B], +T_TALLIER <: Tallier[B, A], B, G, A : Monoid] private (
                                                                                                                            grouper: T_GROUPER,
                                                                                                                            underlyingTallier: T_TALLIER,
                                                                                                                          ) extends Tallier[B, Tally[G, A]] {
  override def tallyAll(ballots: Iterable[B]): Tally[G, A] = {
    val mapBuilder: mutable.Map[G, A] = mutable.Map[G, A]()

    mapBuilder.sizeHint(ballots.size)

    for (ballot <- ballots) {
      val groups = grouper.groupsOf(ballot)
      val count = underlyingTallier.tally(ballot)

      for (group <- groups) {
        val existingValue = mapBuilder.getOrElse(group, Monoid[A].empty)

        mapBuilder.update(group, Monoid[A].combine(existingValue, count))
      }
    }

    Tally(mapBuilder.toMap)
  }
}

object GroupAndCountTallier {

  type With1Tier[T_GROUPER_1 <: Grouper[G1, B], T_TALLIER <: Tallier[B, A], B, G1, A] = GroupAndCountTallier[T_GROUPER_1, T_TALLIER, B, G1, A]
  type With2Tier[T_GROUPER_1 <: Grouper[G1, B], T_GROUPER_2 <: Grouper[G2, B], T_TALLIER <: Tallier[B, A], B, G1, G2, A] = GroupAndCountTallier[T_GROUPER_1, With1Tier[T_GROUPER_2, T_TALLIER, B, G2, A], B, G1, Tally1[G2, A]]
  type With3Tier[T_GROUPER_1 <: Grouper[G1, B], T_GROUPER_2 <: Grouper[G2, B], T_GROUPER_3 <: Grouper[G3, B], T_TALLIER <: Tallier[B, A], B, G1, G2, G3, A] = GroupAndCountTallier[T_GROUPER_1, With2Tier[T_GROUPER_2, T_GROUPER_3, T_TALLIER, B, G2, G3, A], B, G1, Tally2[G2, G3, A]]
  type With4Tier[T_GROUPER_1 <: Grouper[G1, B], T_GROUPER_2 <: Grouper[G2, B], T_GROUPER_3 <: Grouper[G3, B], T_GROUPER_4 <: Grouper[G4, B], T_TALLIER <: Tallier[B, A], B, G1, G2, G3, G4, A] = GroupAndCountTallier[T_GROUPER_1, With3Tier[T_GROUPER_2, T_GROUPER_3, T_GROUPER_4, T_TALLIER, B, G2, G3, G4, A], B, G1, Tally3[G2, G3, G4, A]]

  def with1Tier[T_GROUPER_1 <: Grouper[G1, B], T_TALLIER <: Tallier[B, A], B, G1, A : Monoid]
  (
    grouper: T_GROUPER_1,
    tallier: T_TALLIER,
  ): GroupAndCountTallier.With1Tier[T_GROUPER_1, T_TALLIER, B, G1, A] = GroupAndCountTallier(grouper, tallier)

  def with2Tier[T_GROUPER_1 <: Grouper[G1, B], T_GROUPER_2 <: Grouper[G2, B], T_TALLIER <: Tallier[B, A], B, G1, G2, A : Monoid]
  (
    grouper1: T_GROUPER_1,
    grouper2: T_GROUPER_2,
    tallier: T_TALLIER,
  ): GroupAndCountTallier.With2Tier[T_GROUPER_1, T_GROUPER_2, T_TALLIER, B, G1, G2, A] =
    GroupAndCountTallier(grouper1, with1Tier(grouper2, tallier))

  def with3Tier[T_GROUPER_1 <: Grouper[G1, B], T_GROUPER_2 <: Grouper[G2, B], T_GROUPER_3 <: Grouper[G3, B], T_TALLIER <: Tallier[B, A], B, G1, G2, G3, A : Monoid]
  (
    grouper1: T_GROUPER_1,
    grouper2: T_GROUPER_2,
    grouper3: T_GROUPER_3,
    tallier: T_TALLIER,
  ): GroupAndCountTallier.With3Tier[T_GROUPER_1, T_GROUPER_2, T_GROUPER_3, T_TALLIER, B, G1, G2, G3, A] =
    GroupAndCountTallier(grouper1, with2Tier(grouper2, grouper3, tallier))

  def with4Tier[T_GROUPER_1 <: Grouper[G1, B], T_GROUPER_2 <: Grouper[G2, B], T_GROUPER_3 <: Grouper[G3, B], T_GROUPER_4 <: Grouper[G4, B], T_TALLIER <: Tallier[B, A], B, G1, G2, G3, G4, A : Monoid]
  (
    grouper1: T_GROUPER_1,
    grouper2: T_GROUPER_2,
    grouper3: T_GROUPER_3,
    grouper4: T_GROUPER_4,
    tallier: T_TALLIER,
  ): GroupAndCountTallier.With4Tier[T_GROUPER_1, T_GROUPER_2, T_GROUPER_3, T_GROUPER_4, T_TALLIER, B, G1, G2, G3, G4, A] =
    GroupAndCountTallier(grouper1, with3Tier(grouper2, grouper3, grouper4, tallier))

  implicit def encoder[T_GROUPER <: Grouper[_, _] : Encoder, T_TALLIER <: Tallier[_, _] : Encoder]: Encoder[GroupAndCountTallier[T_GROUPER, T_TALLIER, _, _, _]] =
    Encoder.forProduct2("grouper", "tallier")(t => (t.grouper, t.underlyingTallier))

  implicit def decoder[B, T_GROUPER <: Grouper[G, B] : Decoder, G, T_TALLIER <: Tallier[B, A] : Decoder, A : Monoid](implicit
                                                                                                                     grouper: T_GROUPER,
                                                                                                                     underlyingTallier: T_TALLIER,
                                                                                                                    ): Decoder[GroupAndCountTallier[T_GROUPER, T_TALLIER, B, G, A]] =
    Decoder.forProduct2[GroupAndCountTallier[T_GROUPER, T_TALLIER, B, G, A], T_GROUPER, T_TALLIER]("grouper", "tallier") {
      case (grouper, tallier) => GroupAndCountTallier[T_GROUPER, T_TALLIER, B, G, A](grouper, tallier)
    }

}


