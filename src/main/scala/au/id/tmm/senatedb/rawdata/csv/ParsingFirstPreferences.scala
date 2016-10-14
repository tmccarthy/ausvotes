package au.id.tmm.senatedb.rawdata.csv

import au.id.tmm.senatedb.rawdata.model.FirstPreferencesRow
import au.id.tmm.utilities.collection.CloseableIterator

import scala.io.Source
import scala.util.Try

object ParsingFirstPreferences {
  def parseLines(csvLines: Source): Try[CloseableIterator[FirstPreferencesRow]] = Try {
    val lineIterator = CsvParseUtil.csvIteratorIgnoringLines(csvLines, numIgnoredLines = 2)

    lineIterator
      .map(parseCsvLine) // Any thrown exceptions will go up to the encompassing Try
  }

  def parseCsvLine(line: Seq[String]): FirstPreferencesRow = FirstPreferencesRow(
    state = line(0),
    ticket = line(1),
    candidateId = line(2),
    ballotPosition = line(3).toInt,
    candidateDetails = line(4),
    party = line(5),
    ordinaryVotes = line(6).toInt,
    absentVotes = line(7).toInt,
    provisionalVotes = line(8).toInt,
    prePollVotes = line(9).toInt,
    postalVotes = line(10).toInt,
    totalVotes = line(10).toInt
  )
}
