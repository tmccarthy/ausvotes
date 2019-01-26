package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.computations.BallotWithFacts
import au.id.tmm.ausvotes.core.tallies.BallotGrouper.groupBallots

import scala.collection.mutable

sealed trait BallotGrouper {

  private[tallies] def subGrouper: BallotGrouper

  def intoGroups(ballots: Iterable[BallotWithFacts]): Any

  def name: String
}

case object BallotGrouper0 extends BallotGrouper {

  private[tallies] override def subGrouper: Nothing = throw new NotImplementedError()

  override def intoGroups(ballots: Iterable[BallotWithFacts]): Iterable[BallotWithFacts] = ballots

  override val name: String = "none"
}

final case class BallotGrouper1[T_GROUP_1] (grouping1: BallotGrouping[T_GROUP_1]) extends BallotGrouper {

  private[tallies] override val subGrouper: BallotGrouper0.type = BallotGrouper0

  override def intoGroups(ballots: Iterable[BallotWithFacts]): Map[T_GROUP_1, Iterable[BallotWithFacts]] =
    groupBallots(ballots, grouping1)
      .mapValues(subGrouper.intoGroups)

  override val name: String = grouping1.name
}

final case class BallotGrouper2[T_GROUP_1, T_GROUP_2] (grouping1: BallotGrouping[T_GROUP_1],
                                                       grouping2: BallotGrouping[T_GROUP_2]) extends BallotGrouper {

  private[tallies] override val subGrouper: BallotGrouper1[T_GROUP_2] = new BallotGrouper1[T_GROUP_2](grouping2)

  override def intoGroups(ballots: Iterable[BallotWithFacts]): Map[T_GROUP_1, Map[T_GROUP_2, Iterable[BallotWithFacts]]] =
    groupBallots(ballots, grouping1)
      .mapValues(subGrouper.intoGroups)

  override val name: String = s"${grouping1.name}, ${grouping2.name}"
}

final case class BallotGrouper3[T_GROUP_1, T_GROUP_2, T_GROUP_3] (grouping1: BallotGrouping[T_GROUP_1],
                                                                  grouping2: BallotGrouping[T_GROUP_2],
                                                                  grouping3: BallotGrouping[T_GROUP_3]) extends BallotGrouper {

  private[tallies] override val subGrouper: BallotGrouper2[T_GROUP_2, T_GROUP_3] = new BallotGrouper2[T_GROUP_2, T_GROUP_3](grouping2, grouping3)

  override def intoGroups(ballots: Iterable[BallotWithFacts]): Map[T_GROUP_1, Map[T_GROUP_2, Map[T_GROUP_3, Iterable[BallotWithFacts]]]] =
    groupBallots(ballots, grouping1)
      .mapValues(subGrouper.intoGroups)

  override val name: String = s"${grouping1.name}, ${grouping2.name}, ${grouping3.name}"
}

final case class BallotGrouper4[T_GROUP_1, T_GROUP_2, T_GROUP_3, T_GROUP_4] (grouping1: BallotGrouping[T_GROUP_1],
                                                                             grouping2: BallotGrouping[T_GROUP_2],
                                                                             grouping3: BallotGrouping[T_GROUP_3],
                                                                             grouping4: BallotGrouping[T_GROUP_4]) extends BallotGrouper {
  private [tallies] override val subGrouper: BallotGrouper3[T_GROUP_2, T_GROUP_3, T_GROUP_4] = new BallotGrouper3[T_GROUP_2, T_GROUP_3, T_GROUP_4](grouping2, grouping3, grouping4)

  override def intoGroups(ballots: Iterable[BallotWithFacts]): Map[T_GROUP_1, Map[T_GROUP_2, Map[T_GROUP_3, Map[T_GROUP_4, Iterable[BallotWithFacts]]]]] =
    groupBallots(ballots, grouping1)
      .mapValues(subGrouper.intoGroups)

  override val name: String = s"${grouping1.name}, ${grouping2.name}, ${grouping3.name}, ${grouping4.name}"
}

object BallotGrouper {
  def groupBallots[A](ballots: Iterable[BallotWithFacts], grouping: BallotGrouping[A]): Map[A, Iterable[BallotWithFacts]] = {
    val builder = mutable.Map.empty[A, mutable.Builder[BallotWithFacts, Iterable[BallotWithFacts]]]

    for (ballot <- ballots) {
      for (group <- grouping.groupsOf(ballot)) {
        val bucket = builder.getOrElseUpdate(group, ballots.genericBuilder)
        bucket += ballot
      }
    }

    val resultBuilder = Map.newBuilder[A, Iterable[BallotWithFacts]]

    for ((group, ballotsBuilder) <- builder) {
      resultBuilder += ((group, ballotsBuilder.result()))
    }

    resultBuilder.result()
  }
}
