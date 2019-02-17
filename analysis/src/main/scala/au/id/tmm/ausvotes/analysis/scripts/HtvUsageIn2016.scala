package au.id.tmm.ausvotes.analysis.scripts

import au.id.tmm.ausvotes.analysis.TallyingAnalysisScript
import au.id.tmm.ausvotes.analysis.models.PartyGroup
import au.id.tmm.ausvotes.analysis.models.ValueTypes.{UsedHtv, VotedFormally}
import au.id.tmm.ausvotes.analysis.utilities.charts.HorizontalStackedBar.XAxisOrdering
import au.id.tmm.ausvotes.analysis.utilities.charts.{HorizontalStackedBar, VerticalGroupedBar}
import au.id.tmm.ausvotes.analysis.utilities.data_processing.Aggregations.AggregationOps
import au.id.tmm.ausvotes.analysis.utilities.data_processing.Joins
import au.id.tmm.ausvotes.analysis.utilities.rendering.MarkdownRendering
import au.id.tmm.ausvotes.analysis.utilities.themes.PlotlyTheme
import au.id.tmm.ausvotes.analysis.utilities.themes.PlotlyTheme._
import au.id.tmm.ausvotes.core.tallies._
import au.id.tmm.ausvotes.core.tallying.FetchTally
import au.id.tmm.ausvotes.core.tallying.impl.FetchTallyImpl
import au.id.tmm.ausvotes.data_sources.aec.federal.extras.FetchSenateHtv
import au.id.tmm.ausvotes.data_sources.aec.federal.parsed.{FetchDivisionsAndFederalPollingPlaces, FetchSenateBallots, FetchSenateCountData, FetchSenateGroupsAndCandidates}
import au.id.tmm.ausvotes.data_sources.aec.federal.raw.impl.FetchRawFederalElectionData
import au.id.tmm.ausvotes.data_sources.common.JsonCache
import au.id.tmm.ausvotes.model.Party
import au.id.tmm.ausvotes.model.StateCodec._
import au.id.tmm.ausvotes.model.federal.Division
import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import au.id.tmm.ausvotes.model.instances.StateInstances
import au.id.tmm.ausvotes.shared.io.instances.ZIOInstances._
import au.id.tmm.ausvotes.shared.io.typeclasses.Concurrent
import au.id.tmm.utilities.geo.australia.State
import cats.Monoid
import cats.instances.double._
import scalaz.zio.IO

object HtvUsageIn2016 extends TallyingAnalysisScript {

  override def run()(
    implicit
    jsonCache: JsonCache[IO],
    fetchRawFederalElectionData: FetchRawFederalElectionData[IO],
    fetchGroupsAndCandidates: FetchSenateGroupsAndCandidates[IO],
    fetchDivisions: FetchDivisionsAndFederalPollingPlaces[IO],
    fetchCountData: FetchSenateCountData[IO],
    fetchSenateBallots: FetchSenateBallots[IO],
    fetchHtv: FetchSenateHtv[IO],
    fetchTallies: FetchTallyImpl,
  ): Unit = {

    val (usedHtv_nationally, votedFormally_nationally, usedHtv_perNationalParty, votedFormally_perNationalParty, usedHtv_perState_perParty, votedFormally_perState_perParty, usedHtv_perState_perDivision_perParty, votedFormally_perState_perDivision_perParty) = unsafeRun {
      Concurrent.par8(
        FetchTally.fetchTally0(SenateElection.`2016`, TallierBuilder.counting(BallotCounter.UsedHowToVoteCard).overall()),
        FetchTally.fetchTally0(SenateElection.`2016`, TallierBuilder.counting(BallotCounter.FormalBallots).overall()),

        FetchTally.fetchTally1(SenateElection.`2016`, TallierBuilder.counting(BallotCounter.UsedHowToVoteCard).groupedBy(BallotGrouping.FirstPreferencedPartyNationalEquivalent)),
        FetchTally.fetchTally1(SenateElection.`2016`, TallierBuilder.counting(BallotCounter.FormalBallots).groupedBy(BallotGrouping.FirstPreferencedPartyNationalEquivalent)),

        FetchTally.fetchTally2(SenateElection.`2016`, TallierBuilder.counting(BallotCounter.UsedHowToVoteCard).groupedBy(BallotGrouping.State, BallotGrouping.FirstPreferencedParty)),
        FetchTally.fetchTally2(SenateElection.`2016`, TallierBuilder.counting(BallotCounter.FormalBallots).groupedBy(BallotGrouping.State, BallotGrouping.FirstPreferencedParty)),

        FetchTally.fetchTally3(SenateElection.`2016`, TallierBuilder.counting(BallotCounter.UsedHowToVoteCard).groupedBy(BallotGrouping.State, BallotGrouping.Division, BallotGrouping.FirstPreferencedParty)),
        FetchTally.fetchTally3(SenateElection.`2016`, TallierBuilder.counting(BallotCounter.FormalBallots).groupedBy(BallotGrouping.State, BallotGrouping.Division, BallotGrouping.FirstPreferencedParty)),
      )
    }

    analysisNationally(usedHtv_nationally, votedFormally_nationally)
    println()
    analysisNationallyByParty(usedHtv_perNationalParty, votedFormally_perNationalParty)
    println()
    analysisByStateByParty(usedHtv_perState_perParty, votedFormally_perState_perParty)
    println()
    analysisByDivision(usedHtv_perState_perDivision_perParty, votedFormally_perState_perDivision_perParty)
  }

  private def analysisNationally(usedHtv_nationally: Tally0, votedFormally_nationally: Tally0): Unit = {
    val usedHtv = UsedHtv.Nominal(usedHtv_nationally.value)
    val votedFormally = VotedFormally(votedFormally_nationally.value)
    val usedHtvPercentage = usedHtv / votedFormally

    println(MarkdownRendering.render("Used HTV", "Voted formally", "% used HTV")(List((usedHtv, votedFormally, usedHtvPercentage))))
  }

  private def analysisNationallyByParty(
                                         usedHtv: Tally1[Option[Party]],
                                         votedFormally: Tally1[Option[Party]],
                                       ): Unit = {
    def prepare[A : Monoid](tally: Tally1[Option[Party]])(makeA: Double => A): Map[PartyGroup, A] =
      tally.asStream
        .map { case (party, Tally0(tallyAsDouble)) => PartyGroup.from(party) -> makeA(tallyAsDouble) }
        .groupByAndAggregate { case (party, _) => party } { case (_, tally) => tally }

    val preparedTallies: List[(PartyGroup, UsedHtv.Nominal, VotedFormally, UsedHtv.Percentage)] = Joins.innerJoinUsing(
      left = prepare[UsedHtv.Nominal](usedHtv)(d => UsedHtv.Nominal(d.toInt)),
      right = prepare[VotedFormally](votedFormally)(d => VotedFormally(d.toInt)),
    ).map { case (partyGroup, usedHtv, votedFormally) =>
      (partyGroup, usedHtv, votedFormally, usedHtv / votedFormally)
    }.sortBy(_._1)

    println(MarkdownRendering.render("Party", "Used HTV", "Voted formally", "% used HTV")(preparedTallies))

    val usedHtvTally = prepare(usedHtv)(UsedHtv.Nominal(_)).map { case (party, tally) => (party, ()) -> tally }
    val votedFormallyTally = prepare(votedFormally)(VotedFormally(_)).map { case (party, tally) => (party, ()) -> (tally.asInt - usedHtvTally((party, ())).asInt).toDouble }

    HorizontalStackedBar.make[PartyGroup, Unit, UsedHtv.Nominal, Double](
      chartTitle = "How to vote card usage by first-preferenced party",
      chartDiv = "htv_usage_by_party",
      xAxisTitle = "Number of votes",
      yAxisTitle = "Party",
      yAxisKeyName = _.name,
      activeKeyName = _ => "HTV used",
      inactiveKeyName = "HTV unused",
      activeCountToDouble = _.asInt.toDouble,
      inactiveCountToDouble = identity,
      activeTallies = usedHtvTally,
      inactiveTallies = votedFormallyTally,
      allActiveKeys = Set(()),
      yAxisOrdering = XAxisOrdering.AccordingTo(PartyGroup.ordering.reverse),
      activeKeyOrdering = Ordering.Unit,
      colourForActiveKey = _ => Some(primaryColour),
      colourForInactiveKey = Some(inactiveColour),
    )

  }

  private def analysisByStateByParty(usedHtv: Tally2[State, Option[Party]], votedFormally: Tally2[State, Option[Party]]): Unit = {
    def prepare[A : Monoid](tally: Tally2[State, Option[Party]])(makeA: Double => A): List[(State, PartyGroup, A)] =
      tally.asStream
        .map { case (state, party, Tally0(tallyAsDouble)) => (state, PartyGroup.from(party), makeA(tallyAsDouble)) }
        .groupByAndAggregate { case (state, party, _) => (state, party) } { case (_, _, tally) => tally }
        .map { case ((state, party), tally) => (state, party, tally) }
        .toList

    val preparedTalliesForTable: List[(State, PartyGroup, UsedHtv.Nominal, VotedFormally, UsedHtv.Percentage)] = Joins.innerJoin(
      left = prepare[UsedHtv.Nominal](usedHtv)(d => UsedHtv.Nominal(d.toInt)),
      right = prepare[VotedFormally](votedFormally)(d => VotedFormally(d.toInt)),
    )(
      { case (state, party, _) => (state, party) },
      { case (state, party, _) => (state, party) },
    ).map { case ((state, party), (_, _, usedHtv), (_, _, votedFormally)) =>
      (state, party, usedHtv, votedFormally, usedHtv / votedFormally)
    }.sortBy { case (state, party, _, _, _) => (state, party) }

    println(MarkdownRendering.render("State", "Party", "Used Htv", "Voted formally", "% used HTV")(preparedTalliesForTable))

    val talliesForChart = preparedTalliesForTable
      .groupBy { case (state, party, _, _, percentage) => state }
      .mapValues { listForParty => listForParty.map { case (state, party, _, _, percentage) => party -> percentage }.toMap }

    VerticalGroupedBar.make[State, PartyGroup, UsedHtv.Percentage](
      chartTitle = "Fraction of voters using a how-to-vote card by state and first-preferenced party",
      chartDiv = "htv_usage_by_state_party",
      xAxisTitle = None,
      yAxisTitle = "% using HTV card",
      barName = party => party.name,
      groupName = state => state.abbreviation,
      countToDouble = _.asDouble,
      tallies = talliesForChart,
      allBarKeys = PartyGroup.all.toSet,
      groupOrdering = StateInstances.orderStatesByPopulation,
      barOrdering = PartyGroup.ordering,
      colourForBar = party => Some(PlotlyTheme.colourFor(party)),
    )
  }

  def analysisByDivision(
                          usedHtv_perState_perDivision_perParty: Tally3[State, Division, Option[Party]],
                          votedFormally_perState_perDivision_perParty: Tally3[State, Division, Option[Party]],
                        ): Unit = {
    def prepare[A : Monoid](tally: Tally3[State, Division, Option[Party]])(makeA: Double => A): Map[(Division, PartyGroup), A] =
      tally.asStream
        .map { case (state, division, party, Tally0(tallyAsDouble)) => (division, PartyGroup.from(party), makeA(tallyAsDouble)) }
        .groupByAndAggregate { case (division, party, _) => (division, party) } { case (_, _, tally) => tally }

    val nominalHtvUsageTallies = prepare(usedHtv_perState_perDivision_perParty)(UsedHtv.Nominal(_))

    HorizontalStackedBar.make[Division, PartyGroup, UsedHtv.Nominal, Double](
      chartTitle = "HTV usage by division",
      chartDiv = "htv_usage_by_division",
      xAxisTitle = "Votes",
      yAxisTitle = "Division",
      yAxisKeyName = division => s"${division.name} (${division.jurisdiction.abbreviation})",
      activeKeyName = party => party.name,
      inactiveKeyName = "HTV unused",
      activeCountToDouble = _.asInt.toDouble,
      inactiveCountToDouble = identity,
      activeTallies = nominalHtvUsageTallies,
      inactiveTallies = prepare(votedFormally_perState_perDivision_perParty)(VotedFormally(_)).map {
        case (key, votedFormally) => key -> (votedFormally.asInt - nominalHtvUsageTallies.getOrElse(key, UsedHtv.Nominal(0)).asInt).toDouble
      },
      allActiveKeys = PartyGroup.all.toSet,
      yAxisOrdering = XAxisOrdering.ByActiveCount,
      activeKeyOrdering = PartyGroup.ordering,
      colourForActiveKey = party => Some(colourFor(party)),
      colourForInactiveKey = Some(inactiveColour),
      height = Some(1600),
      yAxisTickFontSize = Some(7),
    )
  }

}
