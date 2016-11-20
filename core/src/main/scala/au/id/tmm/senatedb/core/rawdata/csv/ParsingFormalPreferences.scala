package au.id.tmm.senatedb.core.rawdata.csv

import au.id.tmm.senatedb.core.rawdata.model.FormalPreferencesRow
import au.id.tmm.utilities.collection.CloseableIterator

import scala.io.Source
import scala.util.Try

object ParsingFormalPreferences {

  def parseLines(csvLines: Source): Try[CloseableIterator[FormalPreferencesRow]] = Try {

    val lineIterator = CsvParseUtil.csvIteratorIgnoringLines(csvLines, numIgnoredLines = 2)

    lineIterator
      .map(parseCsvLine) // Any thrown exceptions will go up to the encompassing Try
  }

  def parseCsvLine(line: Seq[String]): FormalPreferencesRow = FormalPreferencesRow(
    electorateName = line(0),
    voteCollectionPointName = line(1),
    voteCollectionPointId = line(2).toInt,
    batchNumber = line(3).toInt,
    paperNumber = line(4).toInt,
    preferences = line(5)
  )
}
