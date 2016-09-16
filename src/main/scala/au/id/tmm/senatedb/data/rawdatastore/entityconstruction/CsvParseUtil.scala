package au.id.tmm.senatedb.data.rawdatastore.entityconstruction

import au.id.tmm.utilities.collection.CloseableIterator
import com.github.tototoshi.csv.CSVReader

import scala.io.Source

private [entityconstruction] object CsvParseUtil {

  def lineIsBlank(csvLine: Seq[String]): Boolean = csvLine.isEmpty || (csvLine.size == 1 && csvLine.head.isEmpty)

  def csvIteratorIgnoringLines(source: Source, numIgnoredLines: Int): CloseableIterator[Seq[String]] = {
    val csvReader = CSVReader.open(source)

    val iterator = csvReader.iterator
      .drop(numIgnoredLines)
      .filterNot(_.isEmpty)

    CloseableIterator(iterator, source)
  }
}
