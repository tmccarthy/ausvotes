package au.id.tmm.ausvotes.data_sources.common.streaming

import java.io.{IOException, InputStream}
import java.nio.charset.Charset

import au.id.tmm.bfect.catsinterop._
import au.id.tmm.bfect.effects.Sync.{CloseableOps, Ops}
import au.id.tmm.bfect.effects.{Bracket, Sync}
import com.github.tototoshi.csv.{CSVFormat, CSVParser}
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import scala.collection.JavaConverters._
import scala.io.{Source => ScalaSource}

object ReadingInputStreams {

  def streamLines[F[+_, +_] : Sync : Bracket](
                                               makeInputStream: F[IOException, InputStream],
                                               charset: Charset = defaultCharset,
                                             ): F[IOException, fs2.Stream[F[Throwable, +?], String]] = {
    for {
      streamOfSource <- syncCatchIOException {
        fs2.Stream.bracket(
          for {
            inputStream <- makeInputStream
            source = ScalaSource.fromInputStream(inputStream)
          } yield source
        )(source => syncCatchIOException(source.close()))
      }

      streamOfLines = streamOfSource.flatMap(source => fs2.Stream.fromIterator(source.getLines()))

    } yield streamOfLines
  }

  def streamCsv[F[+_, +_] : Sync](
                                   lines: fs2.Stream[F[Throwable, +?], String],
                                   csvFormat: CSVFormat,
                                 ): fs2.Stream[F[Throwable, +?], List[String]] = {
    val parser: CSVParser = new CSVParser(csvFormat)

    lines.evalMap { line =>
      parser.parseLine(line) match {
        case Some(value) => Sync.pure(value)
        case None        => Sync.leftPure(new IOException(s"""Invalid line '$line'""")): F[Throwable, List[String]]
      }
    }
  }

  def streamExcel[F[+_, +_] : Sync : Bracket, A](openInputStream: F[IOException, InputStream], sheetIndex: Int)(parseRow: Vector[Cell] => A): F[Exception, Vector[A]] =
    openInputStream.bracketCloseable { inputStream =>
      for {
        workbook <- Sync.syncException(new XSSFWorkbook(inputStream))

        sheet <- Sync.syncException {
          workbook.getSheetAt(sheetIndex)
        }

        parsedRows = sheet.rowIterator().asScala.map { row =>
          parseRow(row.cellIterator().asScala.toVector)
        }.toVector
      } yield parsedRows
    }

}
