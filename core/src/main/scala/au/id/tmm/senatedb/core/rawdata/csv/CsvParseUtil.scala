package au.id.tmm.senatedb.core.rawdata.csv

import au.id.tmm.utilities.collection.CloseableIterator
import com.github.tototoshi.csv.CSVReader

import scala.io.Source

object CsvParseUtil {

  def lineIsBlank(csvLine: Seq[String]): Boolean = csvLine.isEmpty || (csvLine.size == 1 && csvLine.head.isEmpty)

  def csvIteratorIgnoringLines(source: Source, numIgnoredLines: Int): CloseableIterator[Seq[String]] = {
    val csvReader = CSVReader.open(source)

    val iterator = csvReader.iterator
      .drop(numIgnoredLines)
      .filterNot(lineIsBlank)

    CloseableIterator(iterator, source)
  }
}
