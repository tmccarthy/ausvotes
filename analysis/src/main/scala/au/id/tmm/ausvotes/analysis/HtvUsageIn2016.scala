package au.id.tmm.ausvotes.analysis

import java.nio.file.Paths

import au.id.tmm.ausvotes.core.engine.ParsedDataStore
import au.id.tmm.ausvotes.core.io_actions.FetchTally
import au.id.tmm.ausvotes.core.io_actions.implementations._
import au.id.tmm.ausvotes.core.rawdata.{AecResourceStore, RawDataStore}
import au.id.tmm.ausvotes.core.tallies.{TallierBuilder, _}
import au.id.tmm.ausvotes.model.StateCodec._
import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import au.id.tmm.ausvotes.shared.io.instances.ZIOInstances._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import plotly._
import plotly.element.Orientation
import plotly.layout._
import scalaz.zio.IO

object HtvUsageIn2016 extends SparkAnalysisScript {

  override def run(implicit sparkSession: SparkSession): Unit = {
    import sparkSession.implicits._

    val dataStorePath = Paths.get("rawData")
    val jsonCachePath = Paths.get("rawData").resolve("jsonCache")

    val parsedDataStore = ParsedDataStore(RawDataStore(AecResourceStore.at(dataStorePath)))
    implicit val jsonCache: OnDiskJsonCache = new OnDiskJsonCache(jsonCachePath)

    implicit val fetchGroupsAndCandidates: FetchGroupsAndCandidatesFromParsedDataStore = new FetchGroupsAndCandidatesFromParsedDataStore(parsedDataStore)
    implicit val fetchDivisions: FetchDivisionsAndPollingPlacesFromParsedDataStore = new FetchDivisionsAndPollingPlacesFromParsedDataStore(parsedDataStore)
    implicit val fetchCountData: FetchSenateCountDataFromParsedDataStore = new FetchSenateCountDataFromParsedDataStore(parsedDataStore)
    implicit val fetchHtv: FetchSenateHtvFromHardcoded[IO] = new FetchSenateHtvFromHardcoded[IO]

    implicit val fetchTallies: FetchTallyAsWithComputation = unsafeRun(FetchTallyAsWithComputation(parsedDataStore))

    val (usedHtv_perNationalParty, (votedFormally_perNationalParty, (usedHtv_perState_perParty, (votedFormally_perState_perParty, (usedHtv_perState_perDivision_perParty, votedFormally_perState_perDivision_perParty))))) = unsafeRun {
      FetchTally.fetchTally1(SenateElection.`2016`, TallierBuilder.counting(BallotCounter.UsedHowToVoteCard).groupedBy(BallotGrouping.FirstPreferencedPartyNationalEquivalent)).par(
        FetchTally.fetchTally1(SenateElection.`2016`, TallierBuilder.counting(BallotCounter.FormalBallots).groupedBy(BallotGrouping.FirstPreferencedPartyNationalEquivalent)).par(

          FetchTally.fetchTally2(SenateElection.`2016`, TallierBuilder.counting(BallotCounter.UsedHowToVoteCard).groupedBy(BallotGrouping.State, BallotGrouping.FirstPreferencedParty)).par(
            FetchTally.fetchTally2(SenateElection.`2016`, TallierBuilder.counting(BallotCounter.FormalBallots).groupedBy(BallotGrouping.State, BallotGrouping.FirstPreferencedParty)).par(

              FetchTally.fetchTally3(SenateElection.`2016`, TallierBuilder.counting(BallotCounter.UsedHowToVoteCard).groupedBy(BallotGrouping.State, BallotGrouping.Division, BallotGrouping.FirstPreferencedParty)).par(
                FetchTally.fetchTally3(SenateElection.`2016`, TallierBuilder.counting(BallotCounter.FormalBallots).groupedBy(BallotGrouping.State, BallotGrouping.Division, BallotGrouping.FirstPreferencedParty))
              )))))
    }

    val nationalEquivalentPartyColumn = "party"
    val stateColumn = "state"
    val partyColumn = "party"
    //    val divisionColumn = "division"

    val usedHtvColumn = "votes_used_htv"
    val votedFormallyColumn = "votes_formal"
    val percentUsedHtvColumn = "ratio_used_htv"

    val usedHtvByNationalEquivalentPartyDf = {
      val usedHtvDf = usedHtv_perNationalParty.asStream
        .collect {
          case (Some(party), Tally0(numUsedHtv)) => party.name -> numUsedHtv.toInt
        }.toDF(nationalEquivalentPartyColumn, usedHtvColumn)

      val votedFormallyDf = votedFormally_perNationalParty.asStream
        .collect {
          case (Some(party), Tally0(numVotedFormally)) => party.name -> numVotedFormally.toInt
        }.toDF(nationalEquivalentPartyColumn, votedFormallyColumn)

      usedHtvDf.join(votedFormallyDf, nationalEquivalentPartyColumn)
        .withColumn(percentUsedHtvColumn, round(expr(s"($usedHtvColumn/$votedFormallyColumn) * 100"), scale = 2))
        .sort(desc(percentUsedHtvColumn))
    }

    // Bar chart of percentage who followed HTV per national-equivalent party
    {
      val dfForChart = usedHtvByNationalEquivalentPartyDf
        .sort(asc(percentUsedHtvColumn))
        .filter(s"$percentUsedHtvColumn > 1")

      val usedHtvTrace = Bar(
        x = dfForChart.select(percentUsedHtvColumn).map(r => r.getDouble(0)).collect.toList,
        y = dfForChart.select(nationalEquivalentPartyColumn).map(r => r.getString(0)).collect.toList,
        orientation = Orientation.Horizontal,
        name = "HTV card used",
      )

      val totalFormalVotes = Bar(
        x = dfForChart.select(percentUsedHtvColumn).map(r => 100 - r.getDouble(0)).collect.toList,
        y = dfForChart.select(nationalEquivalentPartyColumn).map(r => r.getString(0)).collect.toList,
        orientation = Orientation.Horizontal,
        name = "HTV card unused",
      )

      Plotly.plot(
        "/tmp/bar_chart",
        //    println(Plotly.jsSnippet(
        //      "national_htv_usage",
        List(usedHtvTrace, totalFormalVotes),
        Layout(
          title = "Fraction of voters using a how-to-vote card by first-preferenced party\n(national equivalent, excluding those less than 1%)",
          xaxis = Axis(
            title = "% using how-to-vote card"
          ),
          yaxis = Axis(
            title = "Party (national equivalent)",
            automargin = true,
          ),
          barmode = BarMode.Stack,
          autosize = true,
          showlegend = false,
        ),
      )
    }
//    )

    {
      val dfForChart = usedHtvByNationalEquivalentPartyDf
        .sort(asc(usedHtvColumn))
        .filter(s"$usedHtvColumn > 1000")

      val usedHtvTrace = Bar(
        x = dfForChart.select(usedHtvColumn).map(r => r.getInt(0)).collect.toList,
        y = dfForChart.select(nationalEquivalentPartyColumn).map(r => r.getString(0)).collect.toList,
        orientation = Orientation.Horizontal,
        name = "HTV card used",
      )

      val totalFormalVotes = Bar(
        x = dfForChart.select(votedFormallyColumn).map(r => r.getInt(0)).collect.toList,
        y = dfForChart.select(nationalEquivalentPartyColumn).map(r => r.getString(0)).collect.toList,
        orientation = Orientation.Horizontal,
        name = "HTV card unused",
      )

      Plotly.plot(
        "/tmp/bar_chart",
        //    println(Plotly.jsSnippet(
        //      "national_htv_usage",
        List(totalFormalVotes, usedHtvTrace),
        Layout(
          title = "Fraction of voters using a how-to-vote card by first-preferenced party\n(national equivalent, excluding those with less than 1k using htv cards)",
          xaxis = Axis(
            title = "Number of votes"
          ),
          yaxis = Axis(
            title = "Party (national equivalent)",
            automargin = true,
          ),
          barmode = BarMode.Overlay,
          autosize = true,
          showlegend = false,
        ),
      )
    }

    println(DfRendering.asMarkdown(usedHtvByNationalEquivalentPartyDf))

    // Table of percentage who followed HTV per state per group

    val usedHtvByStateByPartyDf = {
      val usedHtvDf = usedHtv_perState_perParty.asStream
        .collect {
          case (state, Some(party), Tally0(numUsedHtv)) => (state.abbreviation, party.name, numUsedHtv.toInt)
        }.toDF(stateColumn, partyColumn, usedHtvColumn)

      val votedFormallyDf = votedFormally_perState_perParty.asStream
        .collect {
          case (state, Some(party), Tally0(numVotedFormally)) => (state.abbreviation, party.name, numVotedFormally.toInt)
        }.toDF(stateColumn, partyColumn, votedFormallyColumn)

      usedHtvDf.join(votedFormallyDf, nationalEquivalentPartyColumn)
        .withColumn(percentUsedHtvColumn, round(expr(s"($usedHtvColumn/$votedFormallyColumn) * 100"), scale = 2))
        .sort(asc(stateColumn), desc(usedHtvColumn))
    }

    println(DfRendering.asMarkdown(usedHtvByStateByPartyDf))

    // Bar chart of percentage who followed HTV per state per group

    // Table of percentage who followed HTV per state per division per party, truncated at the top 5 per state

    // Table of percentage who followed HTV per state per division per party, truncated at the bottom 5 per state

  }

}