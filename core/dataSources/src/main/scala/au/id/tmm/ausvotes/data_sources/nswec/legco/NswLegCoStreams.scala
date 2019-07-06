package au.id.tmm.ausvotes.data_sources.nswec.legco

import java.net.URL
import java.nio.file.Path

import au.id.tmm.ausvotes.data_sources.common.UrlUtils.StringOps
import au.id.tmm.ausvotes.data_sources.common.streaming.{OpeningInputStreams, ReadingInputStreams}
import au.id.tmm.ausvotes.data_sources.nswec.legco.NswLegCoStreams.SpreadsheetCell
import au.id.tmm.ausvotes.model.nsw.NswElection
import au.id.tmm.ausvotes.model.nsw.legco._
import au.id.tmm.bfect.effects.Sync.Ops
import au.id.tmm.bfect.effects.{Bracket, Sync}
import com.github.tototoshi.csv.TSVFormat
import fs2.Stream
import org.apache.poi.ss.usermodel.{CellType, Row => ExcelRow}

import scala.collection.JavaConverters._

trait NswLegCoStreams[F[+_, +_]] {

  type FStream[A] = Stream[F[Throwable, +?], A]

  def groupRows(election: NswLegCoElection): FStream[Vector[SpreadsheetCell]]

  def candidateRows(election: NswLegCoElection): FStream[Vector[SpreadsheetCell]]

  def preferenceRows(election: NswLegCoElection): FStream[Vector[String]]

}

object NswLegCoStreams {
  sealed trait SpreadsheetCell

  object SpreadsheetCell {
    case object Empty extends SpreadsheetCell
    final case class WithString(string: String) extends SpreadsheetCell
    final case class WithDouble(double: Double) extends SpreadsheetCell
  }

  final class Live[F[+_, +_] : Sync : Bracket](
    resourceStoreLocation: Path,
    replaceExisting: Boolean,
  ) extends NswLegCoStreams[F] {
    override def groupRows(election: NswLegCoElection): FStream[Vector[SpreadsheetCell]] =
      makeSpreadsheetCells(openGroupAndCandidateSheet(election, sheetIndex = 1))

    override def candidateRows(election: NswLegCoElection): FStream[Vector[SpreadsheetCell]] =
      makeSpreadsheetCells(openGroupAndCandidateSheet(election, sheetIndex = 0))

    private def openGroupAndCandidateSheet(election: NswLegCoElection, sheetIndex: Int): FStream[ExcelRow] =
      Stream.eval {
        for {
          url <- election match {
            case NswLegCoElection(NswElection.`2019`) => Sync.pureCatchException(new URL("https://vtrprodragrsstorage01-secondary.blob.core.windows.net/vtrdata-sg1901/lc/SGE2019%20LC%20Candidates.xlsx?st=2019-03-01T01%3A00%3A00Z&se=2020-03-01T01%3A00%3A00Z&sp=r&sv=2018-03-28&sr=c&sig=KPBiRIYtRCT3aWxdLhdcPWb3qbC3wHubyftHBwIjg2Q%3D"))
            case _ => Sync.leftPure(new Exception(s"Unsupported election $election"))
          }

          localPath <- OpeningInputStreams.downloadToDirectory(url, resourceStoreLocation, replaceExisting)

          rows = ReadingInputStreams.streamExcel(OpeningInputStreams.openFile(localPath), sheetIndex)
        } yield rows
      }.flatten

    private def makeSpreadsheetCells(excelRows: FStream[ExcelRow]): FStream[Vector[SpreadsheetCell]] =
      for {
        excelRow <- excelRows

        cells = excelRow.cellIterator().asScala.toVector

        spreadsheetCells = cells.map { c =>
          c.getCellType match {
            case CellType.NUMERIC => SpreadsheetCell.WithDouble(c.getNumericCellValue)
            case CellType.STRING  => SpreadsheetCell.WithString(c.getStringCellValue)
            case CellType.BLANK   => SpreadsheetCell.Empty
            case _                => SpreadsheetCell.Empty
          }
        }
      } yield spreadsheetCells

    override def preferenceRows(election: NswLegCoElection): FStream[Vector[String]] =
      Stream.eval {
        for {
          urlAndZipName <- election match {
            case NswLegCoElection(NswElection.`2019`) =>
              for {
                url <- Sync.fromEither("https://vtrprodragrsstorage01-secondary.blob.core.windows.net/vtrdata-sg1901/lc/SGE2019%20LC%20Pref%20Data%20Statewide.zip?st=2019-03-01T01%3A00%3A00Z&se=2020-03-01T01%3A00%3A00Z&sp=r&sv=2018-03-28&sr=c&sig=KPBiRIYtRCT3aWxdLhdcPWb3qbC3wHubyftHBwIjg2Q%3D".parseUrl): F[Exception, URL]
                zipEntryName = "SGE2019 LC Pref Data_NA_State.txt"
              } yield (url, zipEntryName)

            case _ => Sync.leftPure(new RuntimeException(s"Cannot download resource for $election"))
          }

          url = urlAndZipName._1
          zipEntryName = urlAndZipName._2

          localPath <- OpeningInputStreams.downloadToDirectory(url, resourceStoreLocation, replaceExisting)

          lines <- ReadingInputStreams.streamLines(OpeningInputStreams.openZipEntry(localPath, zipEntryName))

          csvRows = ReadingInputStreams.streamCsv(lines, new TSVFormat {})
        } yield csvRows
      }.flatten
  }
}
