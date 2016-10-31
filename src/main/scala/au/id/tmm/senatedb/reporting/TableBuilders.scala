package au.id.tmm.senatedb.reporting

import au.id.tmm.senatedb.model.parsing.{BallotGroup, Division, Party}
import au.id.tmm.senatedb.reportwriting.TallyTable
import au.id.tmm.senatedb.reportwriting.TallyTable._
import au.id.tmm.senatedb.tallies.Tallier.{NormalTallier, SimpleTallier, TieredTallier}
import au.id.tmm.senatedb.tallies.{CountFormalBallots, Tallier, Tallies}
import au.id.tmm.utilities.geo.australia.State

object TableBuilders {

  final case class NationalTallyTableBuilder(nationalTallier: SimpleTallier, primaryCountColumnTitle: String) extends TableBuilder {

    override def requiredTalliers: Set[Tallier] = Set(
      nationalTallier,
      CountFormalBallots.Nationally
    )

    override def tableFrom(tallies: Tallies): TallyTable[Any] = {
      val nationalTally = tallies.tallyBy(nationalTallier)
      val totalFormalBallotsTally = tallies.tallyBy(CountFormalBallots.Nationally)

      val columns = Vector(
        EmptyColumn,
        PrimaryCountColumn(primaryCountColumnTitle),
        DenominatorCountColumn("Total formal ballots"),
        FractionColumn()
      )

      TallyTable.totalRowOnly(
        nationalTally.count,
        totalFormalBallotsTally.count,
        columns
      )
    }

    override def tableTitle: String = "National total"
  }

  final case class NationalPerFirstPrefTableBuilder(nationalTallier: SimpleTallier,
                                                    perFirstPrefTallier: NormalTallier[Party],
                                                    primaryCountColumnTitle: String) extends TableBuilder {
    override def requiredTalliers: Set[Tallier] = Set(
      perFirstPrefTallier,
      CountFormalBallots.NationallyByFirstPreference,
      nationalTallier,
      CountFormalBallots.Nationally
    )

    override def tableFrom(tallies: Tallies): TallyTable[Party] = {
      val matchingBallotsPerParty = tallies.tallyBy(perFirstPrefTallier)
      val totalFormalBallotsPerParty = tallies.tallyBy(CountFormalBallots.NationallyByFirstPreference)

      val totalMatchingNationally = tallies.tallyBy(nationalTallier)
      val totalFormalBallotsNationally = tallies.tallyBy(CountFormalBallots.Nationally)

      val columns = Vector(
        PartyNameColumn,
        PrimaryCountColumn(primaryCountColumnTitle),
        DenominatorCountColumn("Total formal ballots for party"),
        FractionColumn()
      )

      TallyTable(
        matchingBallotsPerParty,
        totalFormalBallotsPerParty,
        totalMatchingNationally.count,
        totalFormalBallotsNationally.count,
        columns
      )
    }

    override def tableTitle: String = "Nationally by first-preferenced party"
  }

  final case class PerStateTableBuilder(nationalTallier: SimpleTallier,
                                        perStateTallier: NormalTallier[State],
                                        primaryCountColumnTitle: String) extends TableBuilder {
    override def requiredTalliers: Set[Tallier] = Set(
      perStateTallier,
      CountFormalBallots.ByState,
      nationalTallier,
      CountFormalBallots.Nationally
    )

    override def tableFrom(tallies: Tallies): TallyTable[State] = {
      val matchingBallotsPerParty = tallies.tallyBy(perStateTallier)
      val totalFormalBallotsPerParty = tallies.tallyBy(CountFormalBallots.ByState)

      val totalMatchingNationally = tallies.tallyBy(nationalTallier)
      val totalFormalBallotsNationally = tallies.tallyBy(CountFormalBallots.Nationally)

      val columns = Vector(
        StateNameColumn,
        PrimaryCountColumn(primaryCountColumnTitle),
        DenominatorCountColumn("Total formal ballots for party"),
        FractionColumn()
      )

      TallyTable(
        matchingBallotsPerParty,
        totalFormalBallotsPerParty,
        totalMatchingNationally.count,
        totalFormalBallotsNationally.count,
        columns
      )
    }

    override def tableTitle: String = "By state"
  }

  final case class PerDivisionTableBuilder(nationalTallier: SimpleTallier,
                                           perDivisionTallier: NormalTallier[Division],
                                           primaryCountColumnTitle: String) extends TableBuilder {
    override def requiredTalliers: Set[Tallier] = Set(
      perDivisionTallier,
      CountFormalBallots.ByDivision,
      nationalTallier,
      CountFormalBallots.Nationally
    )

    override def tableFrom(tallies: Tallies): TallyTable[Division] = {
      val matchingBallotsPerParty = tallies.tallyBy(perDivisionTallier)
      val totalFormalBallotsPerParty = tallies.tallyBy(CountFormalBallots.ByDivision)

      val totalMatchingNationally = tallies.tallyBy(nationalTallier)
      val totalFormalBallotsNationally = tallies.tallyBy(CountFormalBallots.Nationally)

      val columns = Vector(
        StateNameColumn,
        DivisionNameColumn,
        PrimaryCountColumn(primaryCountColumnTitle),
        DenominatorCountColumn("Total formal ballots for division"),
        FractionColumn()
      )

      TallyTable(
        matchingBallotsPerParty,
        totalFormalBallotsPerParty,
        totalMatchingNationally.count,
        totalFormalBallotsNationally.count,
        columns
      )
    }

    override def tableTitle: String = "By division"
  }

  final case class PerGroupTableBuilder(stateTallier: NormalTallier[State],
                                        perGroupTallier: TieredTallier[State, BallotGroup],
                                        primaryCountColumnTitle: String, state: State) extends TableBuilder {
    override def requiredTalliers: Set[Tallier] = Set(
      perGroupTallier,
      CountFormalBallots.ByFirstPreferencedGroup,
      stateTallier,
      CountFormalBallots.ByState
    )

    override def tableFrom(tallies: Tallies): TallyTable[BallotGroup] = {
      val matchingBallotsInStateByGroup = tallies.tallyBy(perGroupTallier)(state)
      val totalFormalBallotsInStateByGroup = tallies.tallyBy(CountFormalBallots.ByFirstPreferencedGroup)(state)

      val totalMatchingInState = tallies.tallyBy(stateTallier)(state)
      val totalFormalBallotsInState = tallies.tallyBy(CountFormalBallots.ByState)(state)

      val columns = Vector(
        GroupNameColumn,
        PrimaryCountColumn(primaryCountColumnTitle),
        DenominatorCountColumn("Total formal ballots for group"),
        FractionColumn()
      )

      TallyTable(
        matchingBallotsInStateByGroup,
        totalFormalBallotsInStateByGroup,
        totalMatchingInState,
        totalFormalBallotsInState,
        columns
      )
    }

    override def tableTitle: String = s"By group in ${state.name}"
  }
}
