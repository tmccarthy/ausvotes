package au.id.tmm.ausvotes.analysis.scripts

import au.id.tmm.ausvotes.analysis.TallyingAnalysisScript
import au.id.tmm.ausvotes.analysis.models.PartyGroup
import au.id.tmm.ausvotes.analysis.models.ValueTypes.{UsedHtv, VotedFormally}
import au.id.tmm.ausvotes.analysis.utilities.data_processing.Aggregations.AggregationOps
import au.id.tmm.ausvotes.analysis.utilities.data_processing.Joins
import au.id.tmm.ausvotes.analysis.utilities.rendering.MarkdownRendering
import au.id.tmm.ausvotes.core.io_actions.FetchTally
import au.id.tmm.ausvotes.core.io_actions.implementations._
import au.id.tmm.ausvotes.core.tallies._
import au.id.tmm.ausvotes.model.Party
import au.id.tmm.ausvotes.model.StateCodec._
import au.id.tmm.ausvotes.model.federal.Division
import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import au.id.tmm.utilities.geo.australia.State
import cats.Monoid
import plotly.element.{Color, Marker, OneOrSeq, Orientation}
import plotly.layout.{Axis, BarMode, Layout}
import plotly.{Bar, Plotly}
import scalaz.zio.IO

object HtvUsageIn2016 extends TallyingAnalysisScript {

  override def run()(
    implicit
    jsonCache: OnDiskJsonCache,
    fetchGroupsAndCandidates: FetchGroupsAndCandidatesFromParsedDataStore,
    fetchDivisions: FetchDivisionsAndPollingPlacesFromParsedDataStore,
    fetchCountData: FetchSenateCountDataFromParsedDataStore,
    fetchHtv: FetchSenateHtvFromHardcoded[IO],
    fetchTallies: FetchTallyAsWithComputation
  ): Unit = {

    val (usedHtv_perNationalParty, (votedFormally_perNationalParty, (usedHtv_perState_perParty, (votedFormally_perState_perParty, (usedHtv_perState_perDivision_perParty, votedFormally_perState_perDivision_perParty))))) = unsafeRun {
      FetchTally.fetchTally1(SenateElection.`2016`, TallierBuilder.counting(BallotCounter.UsedHowToVoteCard).groupedBy(BallotGrouping.FirstPreferencedPartyNationalEquivalent)).par(
        FetchTally.fetchTally1(SenateElection.`2016`, TallierBuilder.counting(BallotCounter.FormalBallots).groupedBy(BallotGrouping.FirstPreferencedPartyNationalEquivalent)).par(

          FetchTally.fetchTally2(SenateElection.`2016`, TallierBuilder.counting(BallotCounter.UsedHowToVoteCard).groupedBy(BallotGrouping.State, BallotGrouping.FirstPreferencedParty)).par(
            FetchTally.fetchTally2(SenateElection.`2016`, TallierBuilder.counting(BallotCounter.FormalBallots).groupedBy(BallotGrouping.State, BallotGrouping.FirstPreferencedParty)).par(

              FetchTally.fetchTally3(SenateElection.`2016`, TallierBuilder.counting(BallotCounter.UsedHowToVoteCard).groupedBy(BallotGrouping.State, BallotGrouping.Division, BallotGrouping.FirstPreferencedParty)).par(
                FetchTally.fetchTally3(SenateElection.`2016`, TallierBuilder.counting(BallotCounter.FormalBallots).groupedBy(BallotGrouping.State, BallotGrouping.Division, BallotGrouping.FirstPreferencedParty))
              )))))
    }

    analysisNationallyByParty(usedHtv_perNationalParty, votedFormally_perNationalParty)
    analysisByStateByParty(usedHtv_perState_perParty, votedFormally_perState_perParty)
    analysisByDivision(usedHtv_perState_perDivision_perParty, votedFormally_perState_perDivision_perParty)
  }

  private def analysisNationallyByParty(
                                         usedHtv: Tally1[Option[Party]],
                                         votedFormally: Tally1[Option[Party]],
                                       ): Unit = {
    def prepare[A : Monoid](tally: Tally1[Option[Party]])(makeA: Double => A): List[(PartyGroup, A)] =
      tally.asStream
        .map { case (party, Tally0(tallyAsDouble)) => PartyGroup.from(party) -> makeA(tallyAsDouble) }
        .groupByAndAggregate { case (party, _) => party } { case (_, tally) => tally }
        .toList

    val preparedTallies: List[(PartyGroup, UsedHtv.Nominal, VotedFormally, UsedHtv.Percentage)] = Joins.innerJoinUsing(
      left = prepare[UsedHtv.Nominal](usedHtv)(d => UsedHtv.Nominal(d.toInt)),
      right = prepare[VotedFormally](votedFormally)(d => VotedFormally(d.toInt)),
    ).map { case (partyGroup, usedHtv, votedFormally) =>
      (partyGroup, usedHtv, votedFormally, usedHtv / votedFormally)
    }.sortBy(_._1).reverse

    println(MarkdownRendering.render(("Party", "Used HTV", "Voted formally", "% used HTV"))(preparedTallies))

    {
      val usedHtvTrace = Bar(
        x = preparedTallies.map { case (_, usedHtv, _, _) => usedHtv.asInt },
        y = preparedTallies.map { case (party, _, _, _) => party.name },
        orientation = Orientation.Horizontal,
        name = "HTV card used",
      )

      val totalFormalVotesTrace = Bar(
        x = preparedTallies.map { case (_, _, votedFormally, _) => votedFormally.asInt },
        y = preparedTallies.map { case (party, _, _, _) => party.name },
        orientation = Orientation.Horizontal,
        name = "HTV card unused",
      )

      Plotly.plot(
        "/tmp/national_htv_usage",
        //    println(Plotly.jsSnippet(
        //      "national_htv_usage",
        List(usedHtvTrace, totalFormalVotesTrace),
        Layout(
          title = "Fraction of voters using a how-to-vote card by first-preferenced party",
          xaxis = Axis(
            title = "% using how-to-vote card"
          ),
          yaxis = Axis(
            title = "Party",
            automargin = true,
          ),
          barmode = BarMode.Stack,
          autosize = true,
          showlegend = true,
        ),
      )
    }

  }

  private def analysisByStateByParty(usedHtv: Tally2[State, Option[Party]], votedFormally: Tally2[State, Option[Party]]): Unit = {
    def prepare[A : Monoid](tally: Tally2[State, Option[Party]])(makeA: Double => A): List[(State, PartyGroup, A)] =
      tally.asStream
        .map { case (state, party, Tally0(tallyAsDouble)) => (state, PartyGroup.from(party), makeA(tallyAsDouble)) }
        .groupByAndAggregate { case (state, party, _) => (state, party) } { case (_, _, tally) => tally }
        .map { case ((state, party), tally) => (state, party, tally) }
        .toList

    val preparedTallies: List[(State, PartyGroup, UsedHtv.Nominal, VotedFormally, UsedHtv.Percentage)] = Joins.innerJoin(
      left = prepare[UsedHtv.Nominal](usedHtv)(d => UsedHtv.Nominal(d.toInt)),
      right = prepare[VotedFormally](votedFormally)(d => VotedFormally(d.toInt)),
    )(
      { case (state, party, _) => (state, party) },
      { case (state, party, _) => (state, party) },
    ).map { case ((state, party), (_, _, usedHtv), (_, _, votedFormally)) =>
      (state, party, usedHtv, votedFormally, usedHtv / votedFormally)
    }

    println(MarkdownRendering.render(("State", "Party", "Used Htv", "Voted formally", "% used HTV"))(preparedTallies))

    {
      val traces: List[Bar] = PartyGroup.all.map { party =>
        Bar(
          x = preparedTallies.filter { case (_, partyForTally, _, _, _) => party == partyForTally }.map(_._1.abbreviation),
          y = preparedTallies.filter { case (_, partyForTally, _, _, _) => party == partyForTally }.map(_._5.asDouble),
          orientation = Orientation.Vertical,
          name = party.name,
          marker = Marker(
            color = OneOrSeq.One(Color.StringColor(party.colourName)),
          )
        )
      }

      Plotly.plot(
        "/tmp/htv_per_state_per_party",
        //    println(Plotly.jsSnippet(
        //      "htv_per_state_per_party",
        traces,
        Layout(
          title = "Fraction of voters using a how-to-vote card by state, first-preferenced party",
          xaxis = Axis(
            title = "State",
            automargin = true,
          ),
          yaxis = Axis(
            title = "% using HTV card"
          ),
          barmode = BarMode.Group,
          autosize = true,
          showlegend = true,
        ),
      )
    }
  }

  def analysisByDivision(
                          usedHtv_perState_perDivision_perParty: Tally3[State, Division, Option[Party]],
                          votedFormally_perState_perDivision_perParty: Tally3[State, Division, Option[Party]],
                        ): Unit = ???

}
