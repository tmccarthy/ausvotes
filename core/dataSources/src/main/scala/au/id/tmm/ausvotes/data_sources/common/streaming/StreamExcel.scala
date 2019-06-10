package au.id.tmm.ausvotes.data_sources.common.streaming

import java.io.{IOException, InputStream}

import au.id.tmm.bfect.effects.Sync
import au.id.tmm.bfect.effects.Sync.Ops
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import scala.collection.JavaConverters._

object StreamExcel {

  def streamExcel[F[+_, +_] : Sync, A](openInputStream: F[IOException, InputStream], sheetIndex: Int)(parseRow: Vector[Cell] => A): F[Exception, Vector[A]] =
    for {
      inputStream <- openInputStream: F[Exception, InputStream]
      workbook <- Sync.syncException(new XSSFWorkbook(inputStream))

      sheet <- Sync.syncException {
        workbook.getSheetAt(sheetIndex)
      }

      parsedRows = sheet.rowIterator().asScala.map { row =>
        parseRow(row.cellIterator().asScala.toVector)
      }.toVector
    } yield parsedRows

}
