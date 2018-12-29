package au.id.tmm.ausvotes.core.mainclasses

import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import au.id.tmm.ausvotes.core.engine.{ParsedDataStore, ReportEngine, TallyEngine}
import au.id.tmm.ausvotes.core.rawdata.{AecResourceStore, RawDataStore}
import au.id.tmm.ausvotes.core.reporting._
import au.id.tmm.ausvotes.core.reportwriting.ReportWriter
import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import au.id.tmm.utilities.geo.australia.State
import com.google.common.base.Stopwatch

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object WriteReports {

  def main(args: Array[String]): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global

    val stopwatch = Stopwatch.createStarted()

    try {
      val outputPath = Paths.get("reports").resolve(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()))

      val aecResourceStore = AecResourceStore.at(Paths.get("rawData"))
      val rawDataStore = RawDataStore(aecResourceStore)
      val parsedDataStore = ParsedDataStore(rawDataStore)

      val reporters: Set[ReportBuilder] = Set(
        OneAtlReportBuilder,
        DonkeyVoteReportBuilder,
        HowToVoteUsageReportBuilder,
        SavedBallotsReportBuilder,
        AtlVoteReportBuilder,
        AtlAndBtlVoteReportBuilder,
        ExhaustedVotesReportBuilder,
        ExhaustedBallotsReportBuilder
      )

      val executionFuture = ReportEngine.runFor(parsedDataStore,
        TallyEngine,
        SenateElection.`2016`,
        State.ALL_STATES,
        reporters
      )
        .map(reports => {
          reports.foreach(ReportWriter.writeReport(outputPath, _))
        })

      Await.result(executionFuture, Duration.Inf)
    } finally {
      println(s"Completed in $stopwatch")
    }

  }
}
