package au.id.tmm.ausvotes.data_sources.common

import cats.Monad
import cats.Monad.ops._
import cats.effect.Sync
import com.github.tototoshi.csv.CSVReader
import fs2.Stream

import scala.io.Source

private[data_sources] object CsvStreaming {

  def from[F[_] : Sync : Monad](acquireSource: F[Source]): Stream[F, List[String]] = Stream.bracket {
    acquireSource.map(CSVReader.open)
  } { reader =>
    Sync[F].delay {
      reader.close()
    }
  }.flatMap { reader =>
    Stream.fromIterator[F, List[String]](reader.iterator.map(_.toList))(Sync[F])
  }

}
