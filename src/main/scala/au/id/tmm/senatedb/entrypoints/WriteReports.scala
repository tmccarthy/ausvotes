package au.id.tmm.senatedb.entrypoints

import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import au.id.tmm.senatedb.engine.{ParsedDataStore, ReportEngine}
import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.rawdata.{AecResourceStore, RawDataStore}
import au.id.tmm.senatedb.reportwriting.ReportWriter
import au.id.tmm.utilities.geo.australia.State
import com.google.common.base.Stopwatch

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object WriteReports {

  def main(args: Array[String]): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global

    val stopwatch = Stopwatch.createStarted()

    val outputPath = Paths.get("reports").resolve(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()))

    val aecResourceStore = AecResourceStore.at(Paths.get("rawData"))
    val rawDataStore = RawDataStore(aecResourceStore)
    val parsedDataStore = ParsedDataStore(rawDataStore)

    val executionFuture = ReportEngine.runFor(parsedDataStore, SenateElection.`2016`, State.ALL_STATES)
      .flatMap(reportHolder => {
        ReportWriter.writeReports(outputPath, reportHolder)
      })

    Await.result(executionFuture, Duration.Inf)

    println(s"Completed in $stopwatch")
  }

}
