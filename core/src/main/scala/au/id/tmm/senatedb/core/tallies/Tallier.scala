package au.id.tmm.senatedb.core.tallies

import au.id.tmm.senatedb.core.computations.BallotWithFacts

// TODO visibility

sealed trait Tallier {
  type ProducedTallyType <: Tally

  private[tallies] def subTallier: Tallier

  def tally(ballots: Iterable[BallotWithFacts]): ProducedTallyType

  def isOfProducedTallyType(tally: Tally): Boolean
}

final case class Tallier0(ballotCounter: BallotCounter) extends Tallier {

  override type ProducedTallyType = Tally0

  private[tallies] override def subTallier: Nothing = throw new NotImplementedError()

  override def tally(ballots: Iterable[BallotWithFacts]): Tally0 = {
    val groupedBallots = BallotGrouper0.intoGroups(ballots)

    val rawTally = ballotCounter.weigh(groupedBallots)

    Tally0(rawTally)
  }

  override def isOfProducedTallyType(tally: Tally): Boolean = tally.isInstanceOf[Tally0]
}

final case class Tallier1[T_GROUP_1](ballotGrouper: BallotGrouper1[T_GROUP_1], ballotCounter: BallotCounter) extends Tallier {

  override type ProducedTallyType = Tally1[T_GROUP_1]

  private[tallies] override val subTallier: Tallier0 = Tallier0(ballotCounter)

  private[tallies] def tallyGrouped(groupedBallots: Map[T_GROUP_1, Iterable[BallotWithFacts]]): Tally1[T_GROUP_1] = {
    Tally1(groupedBallots.mapValues(subTallier.tally))
  }

  override def tally(ballots: Iterable[BallotWithFacts]): Tally1[T_GROUP_1] = {

    val groupedBallots = ballotGrouper.intoGroups(ballots)

    Tally1(groupedBallots.mapValues(subTallier.tally))
  }

  override def isOfProducedTallyType(tally: Tally): Boolean = tally.isInstanceOf[Tally1[T_GROUP_1]]
}

final case class Tallier2[T_GROUP_1, T_GROUP_2](ballotGrouper: BallotGrouper2[T_GROUP_1, T_GROUP_2], ballotCounter: BallotCounter) extends Tallier {

  override type ProducedTallyType = Tally2[T_GROUP_1, T_GROUP_2]

  private[tallies] override val subTallier: Tallier1[T_GROUP_2] = Tallier1(ballotGrouper.subGrouper, ballotCounter)

  private[tallies] def tallyGrouped(groupedBallots: Map[T_GROUP_1, Map[T_GROUP_2, Iterable[BallotWithFacts]]]): Tally2[T_GROUP_1, T_GROUP_2] = {
    Tally2(groupedBallots.mapValues(subTallier.tallyGrouped))
  }

  override def tally(ballots: Iterable[BallotWithFacts]): Tally2[T_GROUP_1, T_GROUP_2] = {

    val groupedBallots = ballotGrouper.intoGroups(ballots)

    Tally2(groupedBallots.mapValues(subTallier.tallyGrouped))
  }

  override def isOfProducedTallyType(tally: Tally): Boolean = tally.isInstanceOf[Tally2[T_GROUP_1, T_GROUP_2]]
}

final case class Tallier3[T_GROUP_1, T_GROUP_2, T_GROUP_3](ballotGrouper: BallotGrouper3[T_GROUP_1, T_GROUP_2, T_GROUP_3], ballotCounter: BallotCounter) extends Tallier {

  override type ProducedTallyType = Tally3[T_GROUP_1, T_GROUP_2, T_GROUP_3]

  private[tallies] override val subTallier: Tallier2[T_GROUP_2, T_GROUP_3] = Tallier2(ballotGrouper.subGrouper, ballotCounter)

  private[tallies] def tallyGrouped(groupedBallots: Map[T_GROUP_1, Map[T_GROUP_2, Map[T_GROUP_3, Iterable[BallotWithFacts]]]]): Tally3[T_GROUP_1, T_GROUP_2, T_GROUP_3] = {
    Tally3(groupedBallots.mapValues(subTallier.tallyGrouped))
  }

  override def tally(ballots: Iterable[BallotWithFacts]): Tally3[T_GROUP_1, T_GROUP_2, T_GROUP_3] = {

    val groupedBallots = ballotGrouper.intoGroups(ballots)

    Tally3(groupedBallots.mapValues(subTallier.tallyGrouped))
  }

  override def isOfProducedTallyType(tally: Tally): Boolean = tally.isInstanceOf[Tally3[T_GROUP_1, T_GROUP_2, T_GROUP_3]]
}

final case class Tallier4[T_GROUP_1, T_GROUP_2, T_GROUP_3, T_GROUP_4](ballotGrouper: BallotGrouper4[T_GROUP_1, T_GROUP_2, T_GROUP_3, T_GROUP_4], ballotCounter: BallotCounter) extends Tallier {
  override type ProducedTallyType = Tally4[T_GROUP_1, T_GROUP_2, T_GROUP_3, T_GROUP_4]

  private[tallies] override val subTallier: Tallier3[T_GROUP_2, T_GROUP_3, T_GROUP_4] = Tallier3(ballotGrouper.subGrouper, ballotCounter)

  override def tally(ballots: Iterable[BallotWithFacts]): Tally4[T_GROUP_1, T_GROUP_2, T_GROUP_3, T_GROUP_4] = {

    val groupedBallots = ballotGrouper.intoGroups(ballots)

    Tally4(groupedBallots.mapValues(subTallier.tallyGrouped))
  }

  override def isOfProducedTallyType(tally: Tally): Boolean = tally.isInstanceOf[Tally4[T_GROUP_1, T_GROUP_2, T_GROUP_3, T_GROUP_4]]
}