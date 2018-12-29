package au.id.tmm.ausvotes.core.reporting

import au.id.tmm.ausvotes.core.computations.parties.PartySignificanceComputation
import au.id.tmm.ausvotes.core.reportwriting.table.Column._
import au.id.tmm.ausvotes.core.reportwriting.table.{Column, TallyTable}
import au.id.tmm.ausvotes.core.tallies._
import au.id.tmm.ausvotes.model.federal.{Division, FederalVcp}
import au.id.tmm.ausvotes.model.federal.senate.SenateBallotGroup
import au.id.tmm.ausvotes.model.{Party, PartySignificance}
import au.id.tmm.utilities.geo.australia.State

import scala.collection.mutable

object TableBuilders {

  private val countFormalBallots = TallierBuilder.counting(BallotCounter.FormalBallots)
  private val formalBallotsNationally = countFormalBallots.overall()
  private val formalBallotsNationallyByFirstPreference = countFormalBallots
    .groupedBy(BallotGrouping.FirstPreferencedPartyNationalEquivalent)
  private val formalBallotsByState = countFormalBallots.groupedBy(BallotGrouping.State)

  final case class NationalTallyTableBuilder(nationalTallier: Tallier0, primaryCountColumnTitle: String) extends TableBuilder {

    override def requiredTalliers: Set[Tallier] = {
      Set(
        nationalTallier,
        formalBallotsNationally
      )
    }

    override def tableFrom(tallies: TallyBundle): TallyTable[Any] = {
      val nationalTally = tallies.tallyProducedBy(nationalTallier)
      val totalFormalBallotsTally = tallies.tallyProducedBy(formalBallotsNationally)

      val columns = Vector(
        EmptyColumn,
        PrimaryCountColumn(primaryCountColumnTitle),
        DenominatorCountColumn("Total formal ballots"),
        FractionColumn()
      )

      TallyTable.totalRowOnly(
        nationalTally.value,
        totalFormalBallotsTally.value,
        columns
      )
    }

    override def tableTitle: String = "National total"
  }

  final case class NationalPerFirstPrefTableBuilder(nationalTallier: Tallier0,
                                                    perFirstPrefTallier: Tallier1[Option[Party]],
                                                    primaryCountColumnTitle: String) extends TableBuilder {
    override def requiredTalliers: Set[Tallier] = Set(
      perFirstPrefTallier,
      formalBallotsNationallyByFirstPreference,
      nationalTallier,
      formalBallotsNationally
    )

    override def tableFrom(tallies: TallyBundle): TallyTable[Option[Party]] = {
      val matchingBallotsPerParty = tallies.tallyProducedBy(perFirstPrefTallier)
      val totalFormalBallotsPerParty = tallies.tallyProducedBy(formalBallotsNationallyByFirstPreference)

      val totalMatchingNationally = tallies.tallyProducedBy(nationalTallier)
      val totalFormalBallotsNationally = tallies.tallyProducedBy(formalBallotsNationally)

      val columns = Vector(
        PartyNameColumn,
        PrimaryCountColumn(primaryCountColumnTitle),
        DenominatorCountColumn("Total formal ballots for party"),
        FractionColumn()
      )

      TallyTable[Option[Party]](
        matchingBallotsPerParty,
        totalFormalBallotsPerParty(_).value,
        totalMatchingNationally.value,
        totalFormalBallotsNationally.value,
        columns
      )
    }

    override def tableTitle: String = "Nationally by first-preferenced party"
  }

  final case class NationallyPerPartyTypeTableBuilder(nationalTallier: Tallier0,
                                                      perFirstPrefTallier: Tallier1[Option[Party]],
                                                      primaryCountColumnTitle: String) extends TableBuilder {
    override def requiredTalliers: Set[Tallier] = Set(
      perFirstPrefTallier,
      formalBallotsNationallyByFirstPreference,
      nationalTallier,
      formalBallotsNationally
    )

    override def tableFrom(tallies: TallyBundle): TallyTable[PartySignificance] = {
      val matchingBallotsPerParty = tallies.tallyProducedBy(perFirstPrefTallier)
      val totalFormalBallotsPerParty = tallies.tallyProducedBy(formalBallotsNationallyByFirstPreference)

      val matchingBallotsPerPartyType = convertToBeByPartySignificance(matchingBallotsPerParty)
      val totalFormalBallotsPerPartyType = convertToBeByPartySignificance(totalFormalBallotsPerParty)

      val totalMatchingNationally = tallies.tallyProducedBy(nationalTallier)
      val totalFormalBallotsNationally = tallies.tallyProducedBy(formalBallotsNationally)

      val columns = Vector(
        Column.PartyTypeColumn,
        PrimaryCountColumn(primaryCountColumnTitle),
        DenominatorCountColumn("Total formal ballots"),
        FractionColumn()
      )

      TallyTable[PartySignificance](
        matchingBallotsPerPartyType,
        totalFormalBallotsPerPartyType(_).value,
        totalMatchingNationally.value,
        totalFormalBallotsNationally.value,
        columns
      )
    }

    private def convertToBeByPartySignificance(tallyByParty: Tally1[Option[Party]]): Tally1[PartySignificance] = Tally1 {
      val builder = mutable.Map[PartySignificance, Tally0]().withDefaultValue(Tally0())

      for ((party, tally) <- tallyByParty.asMap) {
        val partySignificance = PartySignificanceComputation.of(party)
        val newValue = builder(partySignificance) + tally

        builder.update(partySignificance, newValue)
      }

      builder.toMap
    }

    override def tableTitle: String = "Nationally by first-preferenced party type"
  }

  final case class PerStateTableBuilder(nationalTallier: Tallier0,
                                        perStateTallier: Tallier1[State],
                                        primaryCountColumnTitle: String) extends TableBuilder {
    override def requiredTalliers: Set[Tallier] = Set(
      perStateTallier,
      formalBallotsByState,
      nationalTallier,
      formalBallotsNationally
    )

    override def tableFrom(tallies: TallyBundle): TallyTable[State] = {
      val matchingBallotsPerParty = tallies.tallyProducedBy(perStateTallier)
      val totalFormalBallotsPerParty = tallies.tallyProducedBy(formalBallotsByState)

      val totalMatchingNationally = tallies.tallyProducedBy(nationalTallier)
      val totalFormalBallotsNationally = tallies.tallyProducedBy(formalBallotsNationally)

      val columns = Vector(
        StateNameColumn,
        PrimaryCountColumn(primaryCountColumnTitle),
        DenominatorCountColumn("Total formal ballots for party"),
        FractionColumn()
      )

      TallyTable[State](
        matchingBallotsPerParty,
        totalFormalBallotsPerParty(_).value,
        totalMatchingNationally.value,
        totalFormalBallotsNationally.value,
        columns
      )
    }

    override def tableTitle: String = "By state"
  }

  final case class PerDivisionTableBuilder(nationalTallier: Tallier0,
                                           perDivisionTallier: Tallier1[Division],
                                           primaryCountColumnTitle: String) extends TableBuilder {
    override def requiredTalliers: Set[Tallier] = Set(
      perDivisionTallier,
      countFormalBallots.groupedBy(BallotGrouping.Division),
      nationalTallier,
      formalBallotsNationally
    )

    override def tableFrom(tallies: TallyBundle): TallyTable[Division] = {
      val matchingBallotsPerDivision = tallies.tallyProducedBy(perDivisionTallier)
      val totalFormalBallotsPerDivision = tallies.tallyProducedBy(countFormalBallots.groupedBy(BallotGrouping.Division))

      val totalMatchingNationally = tallies.tallyProducedBy(nationalTallier)
      val totalFormalBallotsNationally = tallies.tallyProducedBy(formalBallotsNationally)

      val columns = Vector(
        StateNameColumn,
        DivisionNameColumn,
        PrimaryCountColumn(primaryCountColumnTitle),
        DenominatorCountColumn("Total formal ballots for division"),
        FractionColumn()
      )

      TallyTable[Division](
        matchingBallotsPerDivision,
        totalFormalBallotsPerDivision(_).value,
        totalMatchingNationally.value,
        totalFormalBallotsNationally.value,
        columns
      )
    }

    override def tableTitle: String = "By division"
  }

  final case class PerGroupTableBuilder(stateTallier: Tallier1[State],
                                        perGroupTallier: Tallier2[State, SenateBallotGroup],
                                        primaryCountColumnTitle: String,
                                        state: State) extends TableBuilder {
    override def requiredTalliers: Set[Tallier] = Set(
      perGroupTallier,
      countFormalBallots.groupedBy(BallotGrouping.FirstPreferencedGroup),
      stateTallier,
      formalBallotsByState
    )

    override def tableFrom(tallies: TallyBundle): TallyTable[SenateBallotGroup] = {
      val matchingBallotsInStateByGroup = tallies.tallyProducedBy(perGroupTallier)(state)
      val totalFormalBallotsInStateByGroup = tallies
        .tallyProducedBy(countFormalBallots.groupedBy(BallotGrouping.State, BallotGrouping.FirstPreferencedGroup))(state)

      val totalMatchingInState = tallies.tallyProducedBy(stateTallier)(state)
      val totalFormalBallotsInState = tallies.tallyProducedBy(formalBallotsByState)(state)

      val columns = Vector(
        GroupNameColumn,
        PrimaryCountColumn(primaryCountColumnTitle),
        DenominatorCountColumn("Total formal ballots for group"),
        FractionColumn()
      )

      TallyTable[SenateBallotGroup](
        matchingBallotsInStateByGroup,
        totalFormalBallotsInStateByGroup(_).value,
        totalMatchingInState.value,
        totalFormalBallotsInState.value,
        columns
      )
    }

    override def tableTitle: String = s"By group in ${state.toNiceString}"
  }

  final case class PerVoteCollectionPointTableBuilder(nationalTallier: Tallier0,
                                                      perVoteCollectionPointTallier: Tallier1[FederalVcp],
                                                      primaryCountColumnTitle: String) extends TableBuilder {
    override def requiredTalliers: Set[Tallier] = Set(
      perVoteCollectionPointTallier,
      countFormalBallots.groupedBy(BallotGrouping.FirstPreferencedGroup),
      nationalTallier,
      formalBallotsNationally
    )

    override def tableFrom(tallies: TallyBundle): TallyTable[FederalVcp] = {
      val matchingBallotsPerPollingPlace = tallies.tallyProducedBy(perVoteCollectionPointTallier)
      val totalFormalBallotsPerPollingPlace = tallies.tallyProducedBy(countFormalBallots.groupedBy(BallotGrouping.VoteCollectionPoint))

      val totalMatchingNationally = tallies.tallyProducedBy(nationalTallier)
      val totalFormalBallotsNationally = tallies.tallyProducedBy(formalBallotsNationally)

      val columns = Vector(
        StateNameColumn,
        DivisionNameColumn,
        VoteCollectionPointNameColumn,
        PrimaryCountColumn(primaryCountColumnTitle),
        DenominatorCountColumn("Total formal ballots for polling place"),
        FractionColumn()
      )

      TallyTable[FederalVcp](
        matchingBallotsPerPollingPlace,
        totalFormalBallotsPerPollingPlace(_).value,
        totalMatchingNationally.value,
        totalFormalBallotsNationally.value,
        columns
      )
    }

    override def tableTitle: String = "By vote collection point"
  }
}
