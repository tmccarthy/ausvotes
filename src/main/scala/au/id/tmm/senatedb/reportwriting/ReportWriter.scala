package au.id.tmm.senatedb.reportwriting

import java.nio.file.{Files, Path}

import au.id.tmm.senatedb.reporting.TallyReport

import scala.collection.JavaConverters._

object ReportWriter {
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

  def writeTotalVotesReport(reportDir: Path, totalVotesReport: TallyReport): Unit = {
    val fractionsReport = totalVotesReport / totalVotesReport.total

    val title = "Total formal ballots"

    val header = "These tables show the total number of formal ballots."

    val tallyColumnName = "Formal ballots"

    val perStateTable = TallyReportTable(
      totalVotesReport.perState,
      fractionsReport.perState,
      totalVotesReport.total,
      Some(fractionsReport.total),
      colsForPerStateTable(tallyColumnName)
    )

    val perDivisionTable = TallyReportTable(
      totalVotesReport.perDivision,
      fractionsReport.perDivision,
      totalVotesReport.total,
      Some(fractionsReport.total),
      colsForPerDivisionTable(tallyColumnName)
    )

    val perPartyTable = TallyReportTable(
      totalVotesReport.perFirstPreferencedParty,
      fractionsReport.perFirstPreferencedParty,
      totalVotesReport.total,
      Some(fractionsReport.total),
      colsForPerPartyTable(tallyColumnName)
    )

    val perVoteCollectionPointTable = TallyReportTable(
      totalVotesReport.perVoteCollectionPlace,
      fractionsReport.perVoteCollectionPlace,
      totalVotesReport.total,
      Some(fractionsReport.total),
      colsForPerVoteCollectionPlaceTable(tallyColumnName)
    )

    val reportContent = s"""
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

    writeReportTo(reportDir resolve "TotalFormalBallots.md", reportContent)
  }

  private def writeReportTo(reportPath: Path, content: String): Unit = {
    Files.createDirectories(reportPath.getParent)

    Files.deleteIfExists(reportPath)

    Files.write(reportPath, content.lines.toVector.asJava)
  }
}
