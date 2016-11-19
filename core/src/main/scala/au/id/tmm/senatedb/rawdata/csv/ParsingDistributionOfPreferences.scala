package au.id.tmm.senatedb.rawdata.csv

import au.id.tmm.senatedb.rawdata.model.DistributionOfPreferencesRow
import au.id.tmm.utilities.collection.CloseableIterator

import scala.io.Source
import scala.util.Try

object ParsingDistributionOfPreferences {
  def parseLines(csvLines: Source): Try[CloseableIterator[DistributionOfPreferencesRow]] = Try {
    val lineIterator = CsvParseUtil.csvIteratorIgnoringLines(csvLines, numIgnoredLines = 1)

    lineIterator
      .map(parseCsvLine) // Any thrown exceptions will go up to the encompassing Try
  }

  def parseCsvLine(line: Seq[String]): DistributionOfPreferencesRow = DistributionOfPreferencesRow(
    state = line(0),
    numberOfVacancies = line(1).toInt,
    totalFormalPapers = line(2).toInt,
    quota = line(3).toInt,
    count = line(4).toInt,
    ballotPosition = line(5).toInt,
    ticket = line(6),
    surname = line(7),
    givenName = line(8),
    papers = line(9).toInt,
    votesTransferred = line(10).toInt,
    progressiveVoteTotal = line(11).toInt,
    transferValue = line(12).toDouble,
    status = line(13),
    changed = parsePossibleBoolean(line(14)),
    orderElected = line(15).toInt,
    comment = line(16)
  )

  private def parsePossibleBoolean(string: String): Option[Boolean] = {
    if (string.isEmpty) {
      None
    } else {
      Some(string.toBoolean)
    }
  }
}
