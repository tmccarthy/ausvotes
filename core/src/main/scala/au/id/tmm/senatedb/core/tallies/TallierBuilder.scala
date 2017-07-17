package au.id.tmm.senatedb.core.tallies

final class TallierBuilder private (ballotCounter: BallotCounter) {
  def overall(): Tallier0 = Tallier0(ballotCounter)

  def groupedBy[T_GROUP_BY_1](grouping: BallotGrouping[T_GROUP_BY_1]): Tallier1[T_GROUP_BY_1] = {
    val ballotGrouper = BallotGrouper1(grouping)

    Tallier1(ballotGrouper, ballotCounter)
  }

  def groupedBy[T_GROUP_BY_1, T_GROUP_BY_2](grouping1: BallotGrouping[T_GROUP_BY_1],
                                            grouping2: BallotGrouping[T_GROUP_BY_2]): Tallier2[T_GROUP_BY_1, T_GROUP_BY_2] = {
    val ballotGrouper = BallotGrouper2(grouping1, grouping2)

    Tallier2(ballotGrouper, ballotCounter)
  }

  def groupedBy[T_GROUP_BY_1, T_GROUP_BY_2, T_GROUP_BY_3](grouping1: BallotGrouping[T_GROUP_BY_1],
                                                          grouping2: BallotGrouping[T_GROUP_BY_2],
                                                          grouping3: BallotGrouping[T_GROUP_BY_3]): Tallier3[T_GROUP_BY_1, T_GROUP_BY_2, T_GROUP_BY_3] = {
    val ballotGrouper = BallotGrouper3(grouping1, grouping2, grouping3)

    Tallier3(ballotGrouper, ballotCounter)
  }
}

object TallierBuilder {

  def counting(ballotCounter: BallotCounter): TallierBuilder = new TallierBuilder(ballotCounter)

}
