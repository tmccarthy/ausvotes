package au.id.tmm.ausvotes.core.tallies2

import au.id.tmm.ausvotes.core.tallies2.typeclasses.{Grouper, Tallier}
import cats.Monoid
import io.circe.{Decoder, Encoder}

import scala.collection.mutable

final case class GroupAndCountTallier[B, T_GROUPER, G, T_TALLIER, A : Monoid] private (
                                                                                        grouper: T_GROUPER,
                                                                                        tallier: T_TALLIER,
                                                                                      )(implicit
                                                                                        grouperInstance: Grouper[T_GROUPER, G, B],
                                                                                        underlyingTallierInstance: Tallier[T_TALLIER, B, A],
                                                                                      )

object GroupAndCountTallier {

  implicit def groupAndCountTallierIsATallier[B, T_GROUPER, G, T_TALLIER, A : Monoid]
  (implicit
   grouperInstance: Grouper[T_GROUPER, G, B],
   underlyingTallierInstance: Tallier[T_TALLIER, B, A],
  ): Tallier[GroupAndCountTallier[B, T_GROUPER, G, T_TALLIER, A], B, Tally[G, A]] =
    new Tallier[GroupAndCountTallier[B, T_GROUPER, G, T_TALLIER, A], B, Tally[G, A]] {

      private val monoidForA = Monoid[A]

      override def tallyAll(tallier: GroupAndCountTallier[B, T_GROUPER, G, T_TALLIER, A])(ballots: Iterable[B]): Tally[G, A] = {
        val mapBuilder: mutable.Map[G, A] = mutable.Map[G, A]()

        mapBuilder.sizeHint(ballots.size)

        for (ballot <- ballots) {
          val groups = grouperInstance.groupsOf(tallier.grouper)(ballot)
          val count = underlyingTallierInstance.tally(tallier.tallier)(ballot)

          for (group <- groups) {
            val existingValue = mapBuilder.getOrElse(group, monoidForA.empty)

            mapBuilder.update(group, monoidForA.combine(existingValue, count))
          }
        }

        Tally(mapBuilder.toMap)
      }
    }

  type With1Tier[B, T_GROUPER_1, G_1, T_TALLIER, A] = GroupAndCountTallier[B, T_GROUPER_1, G_1, T_TALLIER, A]
  type With2Tier[B, T_GROUPER_1, G_1, T_GROUPER_2, G_2, T_TALLIER, A] = GroupAndCountTallier[B, T_GROUPER_1, G_1, With1Tier[B, T_GROUPER_2, G_2, T_TALLIER, A], Tally1[G_2, A]]
  type With3Tier[B, T_GROUPER_1, G_1, T_GROUPER_2, G_2, T_GROUPER_3, G_3, T_TALLIER, A] = GroupAndCountTallier[B, T_GROUPER_1, G_1, With2Tier[B, T_GROUPER_2, G_2, T_GROUPER_3, G_3, T_TALLIER, A], Tally2[G_2, G_3, A]]
  type With4Tier[B, T_GROUPER_1, G_1, T_GROUPER_2, G_2, T_GROUPER_3, G_3, T_GROUPER_4, G_4, T_TALLIER, A] = GroupAndCountTallier[B, T_GROUPER_1, G_1, With3Tier[B, T_GROUPER_2, G_2, T_GROUPER_3, G_3, T_GROUPER_4, G_4, T_TALLIER, A], Tally3[G_2, G_3, G_4, A]]

  def with1Tier[B, T_GROUPER_1, G_1, T_TALLIER, A : Monoid]
  (
    grouper: T_GROUPER_1,
    tallier: T_TALLIER,
  )(implicit
    grouperInstance: Grouper[T_GROUPER_1, G_1, B],
    underlyingTallierInstance: Tallier[T_TALLIER, B, A],
  ): GroupAndCountTallier.With1Tier[B, T_GROUPER_1, G_1, T_TALLIER, A] = GroupAndCountTallier(grouper, tallier)

  def with2Tier[B, T_GROUPER_1, G_1, T_GROUPER_2, G_2, T_TALLIER, A : Monoid]
  (
    grouper1: T_GROUPER_1,
    grouper2: T_GROUPER_2,
    tallier: T_TALLIER,
  )(implicit
    grouper1Instance: Grouper[T_GROUPER_1, G_1, B],
    grouper2Instance: Grouper[T_GROUPER_2, G_2, B],
    underlyingTallierInstance: Tallier[T_TALLIER, B, A],
  ): GroupAndCountTallier.With2Tier[B, T_GROUPER_1, G_1, T_GROUPER_2, G_2, T_TALLIER, A] =
    GroupAndCountTallier(grouper1, with1Tier(grouper2, tallier))

  def with3Tier[B, T_GROUPER_1, G_1, T_GROUPER_2, G_2, T_GROUPER_3, G_3, T_TALLIER, A : Monoid]
  (
    grouper1: T_GROUPER_1,
    grouper2: T_GROUPER_2,
    grouper3: T_GROUPER_3,
    tallier: T_TALLIER,
  )(implicit
    grouper1Instance: Grouper[T_GROUPER_1, G_1, B],
    grouper2Instance: Grouper[T_GROUPER_2, G_2, B],
    grouper3Instance: Grouper[T_GROUPER_3, G_3, B],
    underlyingTallierInstance: Tallier[T_TALLIER, B, A],
  ): GroupAndCountTallier.With3Tier[B, T_GROUPER_1, G_1, T_GROUPER_2, G_2, T_GROUPER_3, G_3, T_TALLIER, A] =
    GroupAndCountTallier(grouper1, with2Tier(grouper2, grouper3, tallier))

  def with4Tier[B, T_GROUPER_1, G_1, T_GROUPER_2, G_2, T_GROUPER_3, G_3, T_GROUPER_4, G_4, T_TALLIER, A : Monoid]
  (
    grouper1: T_GROUPER_1,
    grouper2: T_GROUPER_2,
    grouper3: T_GROUPER_3,
    grouper4: T_GROUPER_4,
    tallier: T_TALLIER,
  )(implicit
    grouper1Instance: Grouper[T_GROUPER_1, G_1, B],
    grouper2Instance: Grouper[T_GROUPER_2, G_2, B],
    grouper3Instance: Grouper[T_GROUPER_3, G_3, B],
    grouper4Instance: Grouper[T_GROUPER_4, G_4, B],
    underlyingTallierInstance: Tallier[T_TALLIER, B, A],
  ): GroupAndCountTallier.With4Tier[B, T_GROUPER_1, G_1, T_GROUPER_2, G_2, T_GROUPER_3, G_3, T_GROUPER_4, G_4, T_TALLIER, A] =
    GroupAndCountTallier(grouper1, with3Tier(grouper2, grouper3, grouper4, tallier))

  implicit def encoder[T_GROUPER : Encoder, T_TALLIER : Encoder]: Encoder[GroupAndCountTallier[_, T_GROUPER, _, T_TALLIER, _]] =
    Encoder.forProduct2("grouper", "tallier")(t => (t.grouper, t.tallier))

  implicit def decoder[B, T_GROUPER : Decoder, G, T_TALLIER : Decoder, A : Monoid](implicit
                                                                                   grouperInstance: Grouper[T_GROUPER, G, B],
                                                                                   underlyingTallierInstance: Tallier[T_TALLIER, B, A],
                                                                                  ): Decoder[GroupAndCountTallier[B, T_GROUPER, G, T_TALLIER, A]] =
    Decoder.forProduct2[GroupAndCountTallier[B, T_GROUPER, G, T_TALLIER, A], T_GROUPER, T_TALLIER]("grouper", "tallier") {
      case (grouper, tallier) => GroupAndCountTallier[B, T_GROUPER, G, T_TALLIER, A](grouper, tallier)
    }

}


