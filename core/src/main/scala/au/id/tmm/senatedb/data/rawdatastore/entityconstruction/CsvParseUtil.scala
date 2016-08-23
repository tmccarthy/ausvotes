package au.id.tmm.senatedb.data.rawdatastore.entityconstruction

import au.id.tmm.utilities.collection.CloseableIterator
import com.github.tototoshi.csv.CSVReader

import scala.io.Source

private [entityconstruction] object CsvParseUtil {

  def lineIsBlank(csvLine: Seq[String]): Boolean = csvLine.isEmpty || (csvLine.size == 1 && csvLine.head.isEmpty)

  def csvIteratorIgnoringLines(source: Source, linesToIgnore: Set[Int]): CloseableIterator[Seq[String]] = {
    val iterator = CSVReader.open(source)
      .iterator
      .zipWithIndex
      .filterNot { case (_, lineIndex) => linesToIgnore contains lineIndex }
      .map { case (csvLine, _) => csvLine }

    CloseableIterator(iterator, source)
  }

}
