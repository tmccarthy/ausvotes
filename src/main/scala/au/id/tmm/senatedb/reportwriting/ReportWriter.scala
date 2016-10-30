package au.id.tmm.senatedb.reportwriting

import java.nio.charset.Charset
import java.nio.file.{Files, Path}

object ReportWriter {
  def writeReport(outputDir: Path, report: Report): Unit = {
    val fileName = report.title
      .replaceAll("\\s", "_")
      .replaceAll("\\W", "")

    val outputPath = outputDir resolve (fileName + ".md")

    Files.createDirectories(outputDir)

    val charset = Charset.forName("UTF-8")

    Files.write(outputPath, report.asMarkdown.getBytes(charset))
  }
}
