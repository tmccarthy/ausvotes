package au.id.tmm.ausvotes.core.reporting

import au.id.tmm.ausvotes.core.tallies._
import au.id.tmm.ausvotes.model.Party
import au.id.tmm.ausvotes.model.federal.Division
import au.id.tmm.ausvotes.model.federal.senate.SenateBallotGroup
import au.id.tmm.utilities.geo.australia.State

trait PerBallotTallierReportBuilder extends ReportBuilder {

  def primaryCountColumnTitle: String

  override def reportTitle: String

  def ballotCounter: BallotCounter

}

object PerBallotTallierReportBuilder {

  trait IncludesNationalTally { this: PerBallotTallierReportBuilder =>
    final val nationalTallier: Tallier0 = TallierBuilder.counting(ballotCounter).overall()

    val nationalTallyTableBuilder = TableBuilders.NationalTallyTableBuilder(nationalTallier, primaryCountColumnTitle)
  }

  trait IncludesPerFirstPreferenceTally { this: PerBallotTallierReportBuilder with IncludesNationalTally =>
    final def nationalPerFirstPreferenceTallier: Tallier1[Option[Party]] = TallierBuilder.counting(ballotCounter)
      .groupedBy(BallotGrouping.FirstPreferencedPartyNationalEquivalent)

    val perFirstPreferenceTableBuilder = TableBuilders.NationalPerFirstPrefTableBuilder(nationalTallier,
      nationalPerFirstPreferenceTallier, primaryCountColumnTitle)
  }

  trait IncludesPerStateTally { this: PerBallotTallierReportBuilder with IncludesNationalTally =>
    final def perStateTallier: Tallier1[State] = TallierBuilder.counting(ballotCounter)
      .groupedBy(BallotGrouping.State)

    val perStateTableBuilder = TableBuilders.PerStateTableBuilder(nationalTallier, perStateTallier,
      primaryCountColumnTitle)
  }

  trait IncludesPerDivisionTally { this: PerBallotTallierReportBuilder with IncludesNationalTally =>
    final def perDivisionTallier: Tallier1[Division] = TallierBuilder.counting(ballotCounter)
      .groupedBy(BallotGrouping.Division)

    val perDivisionTableBuilder = TableBuilders.PerDivisionTableBuilder(nationalTallier, perDivisionTallier,
      primaryCountColumnTitle)
  }

  trait IncludesPerFirstPreferencedGroupTally { this: PerBallotTallierReportBuilder with IncludesPerStateTally =>
    final def perFirstPreferencedGroupTallier: Tallier2[State, SenateBallotGroup] = TallierBuilder.counting(ballotCounter)
      .groupedBy(BallotGrouping.State, BallotGrouping.FirstPreferencedGroup)

    val perGroupTableBuilders: Vector[TableBuilders.PerGroupTableBuilder] = State.ALL_STATES
      .toVector
      .sorted
      .map(state => TableBuilders.PerGroupTableBuilder(perStateTallier, perFirstPreferencedGroupTallier,
        primaryCountColumnTitle, state)
      )
  }

  trait IncludesPerPartyTypeTally { this: PerBallotTallierReportBuilder =>
    val perPartyTypeTableBuilder = TableBuilders.NationallyPerPartyTypeTableBuilder(
      TallierBuilder.counting(ballotCounter).overall(),
      TallierBuilder.counting(ballotCounter).groupedBy(BallotGrouping.FirstPreferencedPartyNationalEquivalent),
      primaryCountColumnTitle
    )
  }

  trait IncludesPerPollingPlaceTable { this: PerBallotTallierReportBuilder =>
    val perPollingPlaceTableBuilder = TableBuilders.PerVoteCollectionPointTableBuilder(
      TallierBuilder.counting(ballotCounter).overall(),
      TallierBuilder.counting(ballotCounter).groupedBy(BallotGrouping.VoteCollectionPoint),
      primaryCountColumnTitle
    )
  }

  trait IncludesTableByPollingPlace { this: PerBallotTallierReportBuilder =>
    val perPollingPlaceTableBuilder = TableBuilders.PerVoteCollectionPointTableBuilder(
      TallierBuilder.counting(ballotCounter).overall(),
      TallierBuilder.counting(ballotCounter).groupedBy(BallotGrouping.VoteCollectionPoint),
      primaryCountColumnTitle
    )
  }

  trait IncludesTableByPartyType { this: PerBallotTallierReportBuilder =>
    val perPartyTypeTableBuilder = TableBuilders.NationallyPerPartyTypeTableBuilder(
      TallierBuilder.counting(ballotCounter).overall(),
      TallierBuilder.counting(ballotCounter).groupedBy(BallotGrouping.FirstPreferencedPartyNationalEquivalent),
      primaryCountColumnTitle
    )
  }
}
