package au.id.tmm.ausvotes.data_sources.common.streaming

import java.io.{IOException, InputStream}
import java.nio.charset.Charset

import au.id.tmm.bfect.catsinterop._
import au.id.tmm.bfect.effects.Sync.Ops
import au.id.tmm.bfect.effects.{Bracket, Sync}
import com.github.tototoshi.csv.{CSVFormat, CSVParser}
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.{XSSFSheet, XSSFWorkbook}

import scala.collection.JavaConverters._
import scala.io.{Source => ScalaSource}

object ReadingInputStreams {

  // TODO move the effect handling inside the fs2.Stream
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
  ): fs2.Stream[F[Throwable, +?], Vector[String]] = {
    val parser: CSVParser = new CSVParser(csvFormat)

    lines.evalMap { line =>
      parser.parseLine(line) match {
        case Some(value) => Sync.pure(value.toVector)
        case None        => Sync.leftPure(new IOException(s"""Invalid line '$line'""")): F[Throwable, Vector[String]]
      }
    }
  }

  def streamExcel[F[+_, +_] : Sync : Bracket, A](
    openInputStream: F[IOException, InputStream],
    sheetIndex: Int,
  ): fs2.Stream[F[Throwable, +?], Row] =
    fs2.Stream
      .bracket(
        for {
          inputStream <- openInputStream
          workbook <- Sync.syncException(new XSSFWorkbook(inputStream))
        } yield workbook
      )(workbook => Sync.sync(workbook.close()))
      .evalMap(workbook => Sync.syncException(workbook.getSheetAt(sheetIndex)): F[Throwable, XSSFSheet])
      .flatMap(sheet => fs2.Stream.fromIterator[F[Throwable, +?], Row](sheet.rowIterator().asScala))

}
