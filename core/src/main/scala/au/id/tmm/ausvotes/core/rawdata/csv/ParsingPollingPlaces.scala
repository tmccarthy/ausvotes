package au.id.tmm.ausvotes.core.rawdata.csv

import au.id.tmm.ausvotes.core.rawdata.model.PollingPlacesRow
import au.id.tmm.utilities.collection.CloseableIterator

import scala.io.Source
import scala.util.Try

object ParsingPollingPlaces {
  def parseLines(csvLines: Source): Try[CloseableIterator[PollingPlacesRow]] = Try {
    val lineIterator = CsvParseUtil.csvIteratorIgnoringLines(csvLines, numIgnoredLines = 2)

    lineIterator
      .map(parseCsvLine) // Any thrown exceptions will go up to the encompassing Try
  }

  def parseCsvLine(line: Seq[String]): PollingPlacesRow = PollingPlacesRow(
    state = line(0),
    divisionId = line(1).toInt,
    divisionName = line(2),
    pollingPlaceId = line(3).toInt,
    pollingPlaceTypeId = line(4).toInt,
    pollingPlaceName = line(5),
    premisesName = line(6),
    premisesAddress1 = line(7),
    premisesAddress2 = line(8),
    premisesAddress3 = line(9),
    premisesSuburb = line(10),
    premisesState = line(11),
    premisesPostcode = line(12),
    latitude = parsePossibleDouble(line(13)),
    longitude = parsePossibleDouble(line(14))
  )

  private def parsePossibleDouble(string: String): Option[Double] = {
    if (string.isEmpty) {
      None
    } else {
      Some(string.toDouble)
    }
  }
}
