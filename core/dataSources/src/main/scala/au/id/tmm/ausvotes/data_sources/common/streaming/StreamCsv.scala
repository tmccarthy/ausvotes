package au.id.tmm.ausvotes.data_sources.common.streaming

import java.io.IOException

import au.id.tmm.bfect.effects.Sync
import com.github.tototoshi.csv.{CSVFormat, CSVParser}

object StreamCsv {

  def streamCsv[F[+_, +_] : Sync](lines: fs2.Stream[F[Throwable, +?], String], csvFormat: CSVFormat): fs2.Stream[F[Throwable, +?], List[String]] = {
    val parser: CSVParser = new CSVParser(csvFormat)

    lines.evalMap { line =>
      parser.parseLine(line) match {
        case Some(value) => Sync.pure(value)
        case None        => Sync.leftPure(new IOException(s"""Invalid line '$line'""")): F[Throwable, List[String]]
      }
    }
  }

}
