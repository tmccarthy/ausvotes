package au.id.tmm.ausvotes.analysis

import java.nio.file.Paths

import au.id.tmm.ausvotes.core.engine.ParsedDataStore
import au.id.tmm.ausvotes.core.io_actions.FetchTally
import au.id.tmm.ausvotes.core.io_actions.implementations._
import au.id.tmm.ausvotes.core.rawdata.{AecResourceStore, RawDataStore}
import au.id.tmm.ausvotes.core.tallies.{TallierBuilder, _}
import au.id.tmm.ausvotes.model.Party
import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import au.id.tmm.ausvotes.shared.io.instances.ZIOInstances._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}
import scalaz.zio.IO

object HtvUsageIn2016 extends SparkAnalysisScript {

  override def run(implicit sparkSession: SparkSession): Unit = {

    val dataStorePath = Paths.get("rawData")
    val jsonCachePath = Paths.get("rawData").resolve("jsonCache")

    val parsedDataStore = ParsedDataStore(RawDataStore(AecResourceStore.at(dataStorePath)))
    implicit val jsonCache: OnDiskJsonCache = new OnDiskJsonCache(jsonCachePath)

    implicit val fetchGroupsAndCandidates: FetchGroupsAndCandidatesFromParsedDataStore = new FetchGroupsAndCandidatesFromParsedDataStore(parsedDataStore)
    implicit val fetchDivisions: FetchDivisionsAndPollingPlacesFromParsedDataStore = new FetchDivisionsAndPollingPlacesFromParsedDataStore(parsedDataStore)
    implicit val fetchCountData: FetchSenateCountDataFromParsedDataStore = new FetchSenateCountDataFromParsedDataStore(parsedDataStore)
    implicit val fetchHtv: FetchSenateHtvFromHardcoded[IO] = new FetchSenateHtvFromHardcoded[IO]

    implicit val fetchTallies: FetchTallyAsWithComputation = unsafeRun(FetchTallyAsWithComputation(parsedDataStore))

    val (usedHtvTally, votedFormallyTally) = unsafeRun {
      FetchTally.fetchTally1(SenateElection.`2016`, TallierBuilder.counting(BallotCounter.UsedHowToVoteCard).groupedBy(BallotGrouping.FirstPreferencedPartyNationalEquivalent)) par
        FetchTally.fetchTally1(SenateElection.`2016`, TallierBuilder.counting(BallotCounter.FormalBallots).groupedBy(BallotGrouping.FirstPreferencedPartyNationalEquivalent))
    }

    val partyColumn = "party"
    val usedHtvColumn = "votes_used_htv"
    val votedFormallyColumn = "votes_formal"
    val ratioUsedHtvColumn = "ratio_used_htv"

    val usedHtvDf = toDF(usedHtvTally)(partyColumn, usedHtvColumn)
    val formalVotesDf = toDF(votedFormallyTally)(partyColumn, votedFormallyColumn)

    usedHtvDf.join(formalVotesDf, partyColumn)
      .withColumn(ratioUsedHtvColumn, expr(s"($usedHtvColumn/$votedFormallyColumn) * 100"))
      .sort(desc(ratioUsedHtvColumn))
      .show(numRows = 150)

  }

  private def toDF(tally: Tally1[Option[Party]])(partyColumnName: String, votesColumnName: String)(implicit sparkSession: SparkSession): DataFrame = {
    import sparkSession.implicits._

    tally.asStream.map {
      case (Some(party), Tally0(asDouble)) => (party.name, asDouble)
      case (None, Tally0(asDouble)) => ("Independent", asDouble)
    }.toDF(partyColumnName, votesColumnName)
  }

}
