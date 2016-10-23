package au.id.tmm.senatedb.reportwriting

import java.nio.file.{Files, Path}

import au.id.tmm.senatedb.computations.donkeyvotes.DonkeyVoteDetector
import au.id.tmm.senatedb.model.parsing.{Division, Party, VoteCollectionPoint}
import au.id.tmm.senatedb.reporting.{FloatTallyReport, ReportHolder, TallyReport}
import au.id.tmm.utilities.geo.australia.State

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

object ReportWriter {

  def writeReports(reportDir: Path, reportHolder: ReportHolder)(implicit ec: ExecutionContext): Future[Unit] = {
    val totalBallotsWriteFuture = Future(writeTotalBallotsReport(reportDir, reportHolder.totalFormal))
    val oneAtlWriteFuture = Future(writeOneAtlReport(reportDir, reportHolder.oneAtl, reportHolder.totalFormal))
    val donkeyVoteWriteFuture = Future(writeDonkeyVoteReport(reportDir, reportHolder.donkeyVotes, reportHolder.totalFormal))
    val markUsageWriteFuture = Future(writeMarkUsageReport(reportDir, reportHolder.ballotsUsingTicks, reportHolder.ballotsUsingCrosses, reportHolder.totalFormal))

    for {
      _ <- totalBallotsWriteFuture
      _ <- oneAtlWriteFuture
      _ <- donkeyVoteWriteFuture
      _ <- markUsageWriteFuture
    } yield ()
  }

  def writeTotalBallotsReport(reportDir: Path, totalBallotsReport: TallyReport): Unit = {
    val fractionsReport = totalBallotsReport / totalBallotsReport.total

    writeTallyReport(
      reportDir = reportDir,
      reportName = "TotalFormalBallots",
      title = "Total formal ballots",
      header = "These tables show the total number of formal ballots.",
      tallyColumnName = "Formal ballots",
      report = totalBallotsReport,
      fractionsReport = fractionsReport)
  }

  def writeOneAtlReport(reportDir: Path,
                        oneAtlReport: TallyReport,
                        totalBallotsReport: TallyReport): Unit = {
    val fractionsReport = oneAtlReport / totalBallotsReport

    writeTallyReport(
      reportDir,
      "OneATL",
      "Ballots marking just '1' above the line",
      "These table show the total number of ballots marked only with a single '1' above the line. While not in " +
        "compliance with the advice given to voters, these votes were still formal thanks to savings provisions in " +
        "the Electoral Act.",
      "Ballots",
      oneAtlReport,
      fractionsReport
    )
  }

  def writeDonkeyVoteReport(reportDir: Path,
                            donkeyVotes: TallyReport,
                            totalBallotsReport: TallyReport): Unit = {
    val fractionsReport = donkeyVotes / totalBallotsReport

    writeTallyReport(
      reportDir,
      "DonkeyVotes",
      "Donkey votes",
      "These tables show the total number of ballots regarded as 'Donkey Votes'. These are votes that marked " +
        s"at least ${DonkeyVoteDetector.threshold} squares above the line in the order on the ballot paper, and " +
        "marked no squares below the line.",
      "Ballots",
      donkeyVotes,
      fractionsReport
    )
  }

  def writeMarkUsageReport(reportDir: Path,
                           ballotsUsingTicks: TallyReport,
                           ballotsUsingCrosses: TallyReport,
                           totalBallotsReport: TallyReport): Unit = {
    val markedBallotsReport = ballotsUsingTicks + ballotsUsingCrosses

    val fractionsReport = markedBallotsReport / totalBallotsReport

    writeTallyReport(
      reportDir,
      "BallotsWithMarks",
      "Ballots marked with a tick or cross",
      "These tables show the total number of ballots that included a tick or cross. While not in compliance with the " +
        "advice given to voters, these votes were still formal thanks to savings provisions in the Electoral Act. A " +
        "tick or cross, either above or below the line, is considered equivalent to marking the square with a '1'.",
      "Ballots",
      markedBallotsReport,
      fractionsReport
    )
  }

  def writeTallyReport(reportDir: Path,
                       reportName: String,
                       title: String,
                       header: String,
                       tallyColumnName: String,
                       report: TallyReport,
                       fractionsReport: FloatTallyReport): Unit = {

    val perStateTable = TallyReportTable[State](
      report.perState,
      fractionsReport.perState,
      report.total,
      Some(fractionsReport.total),
      colsForPerStateTable(tallyColumnName)
    )

    val perDivisionTable = TallyReportTable[Division](
      report.perDivision,
      fractionsReport.perDivision,
      report.total,
      Some(fractionsReport.total),
      colsForPerDivisionTable(tallyColumnName)
    )

    val perPartyTable = TallyReportTable[Option[Party]](
      report.perFirstPreferencedParty,
      fractionsReport.perFirstPreferencedParty,
      report.total,
      Some(fractionsReport.total),
      colsForPerPartyTable(tallyColumnName)
    )

    val perVoteCollectionPointTable = TallyReportTable[VoteCollectionPoint](
      report.perVoteCollectionPlace,
      fractionsReport.perVoteCollectionPlace,
      report.total,
      Some(fractionsReport.total),
      colsForPerVoteCollectionPlaceTable(tallyColumnName)
    )

    val reportContent = constructReportContent(
      title,
      header,
      perStateTable,
      perDivisionTable,
      perPartyTable,
      perVoteCollectionPointTable)

    writeReportTo(reportDir resolve s"$reportName.md", reportContent)
  }

  private def colsForPerStateTable(tallyColName: String) = Vector(
    TallyReportTable.StateNameColumn,
    TallyReportTable.TallyColumn(tallyColName),
    TallyReportTable.FractionColumn()
  )

  private def colsForPerDivisionTable(tallyColName: String) = Vector(
    TallyReportTable.StateNameColumn,
    TallyReportTable.DivisionNameColumn,
    TallyReportTable.TallyColumn(tallyColName),
    TallyReportTable.FractionColumn()
  )

  private def colsForPerPartyTable(tallyColName: String) = Vector(
    TallyReportTable.PartyNameColumn,
    TallyReportTable.TallyColumn(tallyColName),
    TallyReportTable.FractionColumn()
  )

  private def colsForPerVoteCollectionPlaceTable(tallyColName: String) = Vector(
    TallyReportTable.StateNameColumn,
    TallyReportTable.DivisionNameColumn,
    TallyReportTable.VoteCollectionPointColumn,
    TallyReportTable.TallyColumn(tallyColName),
    TallyReportTable.FractionColumn()
  )

  private def constructReportContent(title: String,
                                     header: String,
                                     perStateTable: TallyReportTable[State],
                                     perDivisionTable: TallyReportTable[Division],
                                     perPartyTable: TallyReportTable[Option[Party]],
                                     perVoteCollectionPointTable: TallyReportTable[VoteCollectionPoint]
                                    ): String = {
    s"""
       |#$title
       |
       |$header
       |
       |### By state
       |
       |${perStateTable.asMarkdown}
       |
       |### By division
       |
       |${perDivisionTable.asMarkdown}
       |
       |### By first preferenced party
       |
       |${perPartyTable.asMarkdown}
       |
       |### By vote-collection point
       |
       |${perVoteCollectionPointTable.asMarkdown}
     """.stripMargin
  }

  private def writeReportTo(reportPath: Path, content: String): Unit = {
    Files.createDirectories(reportPath.getParent)

    Files.deleteIfExists(reportPath)

    Files.write(reportPath, content.lines.toVector.asJava)
  }
}