package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.model.parsing.{BallotGroup, Division, Party}
import au.id.tmm.senatedb.tallies.PerBallotTallier
import au.id.tmm.senatedb.tallies.Tallier.{NormalTallier, SimpleTallier, TieredTallier}
import au.id.tmm.utilities.geo.australia.State

trait PerBallotTallierReportBuilder extends ReportBuilder {
  def primaryCountColumnTitle: String

  override def reportTitle: String

  def perBallotTallier: PerBallotTallier

}

object PerBallotTallierReportBuilder {

  trait IncludesNationalTally { this: PerBallotTallierReportBuilder =>
    final def nationalTallier: SimpleTallier = perBallotTallier.Nationally

    val nationalTallyTableBuilder = TableBuilders.NationalTallyTableBuilder(nationalTallier, primaryCountColumnTitle)
  }

  trait IncludesPerFirstPreferenceTally { this: PerBallotTallierReportBuilder with IncludesNationalTally =>
    final def nationalPerFirstPreferenceTallier: NormalTallier[Party] = perBallotTallier.NationallyByFirstPreference

    val perFirstPreferenceTableBuilder = TableBuilders.NationalPerFirstPrefTableBuilder(nationalTallier,
      nationalPerFirstPreferenceTallier, primaryCountColumnTitle)
  }

  trait IncludesPerStateTally { this: PerBallotTallierReportBuilder with IncludesNationalTally =>
    final def perStateTallier: NormalTallier[State] = perBallotTallier.ByState

    val perStateTableBuilder = TableBuilders.PerStateTableBuilder(nationalTallier, perBallotTallier.ByState,
      primaryCountColumnTitle)
  }

  trait IncludesPerDivisionTally { this: PerBallotTallierReportBuilder with IncludesNationalTally =>
    final def perDivisionTallier: NormalTallier[Division] = perBallotTallier.ByDivision

    val perDivisionTableBuilder = TableBuilders.PerDivisionTableBuilder(nationalTallier, perDivisionTallier,
      primaryCountColumnTitle)
  }

  trait IncludesPerFirstPreferencedGroupTally { this: PerBallotTallierReportBuilder with IncludesPerStateTally =>
    final def perFirstPreferencedGroupTallier: TieredTallier[State, BallotGroup] = perBallotTallier.ByFirstPreferencedGroup

    val perGroupTableBuilders = State.ALL_STATES
      .toVector
      .sorted
      .map(state => TableBuilders.PerGroupTableBuilder(perStateTallier, perFirstPreferencedGroupTallier,
        primaryCountColumnTitle, state)
      )
  }

  trait IncludesPerPartyTypeTally { this: PerBallotTallierReportBuilder =>
    val perPartyTypeTableBuilder = TableBuilders.NationallyPerPartyTypeTableBuilder(
      perBallotTallier.Nationally,
      perBallotTallier.NationallyByFirstPreference,
      primaryCountColumnTitle
    )
  }

  trait IncludesPerPollingPlaceTable { this: PerBallotTallierReportBuilder =>
    val perPollingPlaceTableBuilder = TableBuilders.PerVoteCollectionPointTableBuilder(
      perBallotTallier.Nationally,
      perBallotTallier.ByVoteCollectionPoint,
      primaryCountColumnTitle
    )
  }

  trait IncludesTableByPollingPlace { this: PerBallotTallierReportBuilder =>
    val perPollingPlaceTableBuilder = TableBuilders.PerVoteCollectionPointTableBuilder(
      perBallotTallier.Nationally,
      perBallotTallier.ByVoteCollectionPoint,
      primaryCountColumnTitle
    )
  }

  trait IncludesTableByPartyType { this: PerBallotTallierReportBuilder =>
    val perPartyTypeTableBuilder = TableBuilders.NationallyPerPartyTypeTableBuilder(
      perBallotTallier.Nationally,
      perBallotTallier.NationallyByFirstPreference,
      primaryCountColumnTitle
    )
  }
}