package au.id.tmm.ausvotes.data_sources.common.streaming

import java.io.IOException

import au.id.tmm.bfect.catsinterop._
import au.id.tmm.bfect.testing.BState
import org.scalatest.FlatSpec
import com.github.tototoshi.csv

class ReadingInputStreamsSpec extends FlatSpec {

  private type TestIO[+E, +A] = BState[Unit, E, A]
  private type TestIOTask[+A] = TestIO[Throwable, A]

  "streaming a csv" should "return each row as a list" in {
    val lines: fs2.Stream[TestIOTask, String] = fs2.Stream.emits(Vector(
      "1,2,3",
      "4,5,6",
      "7,8,9",
    ))

    val rows = ReadingInputStreams.streamCsv(lines, csv.defaultCSVFormat).compile.toVector.runEither(())

    assert(rows === Right(Vector(
      List("1", "2", "3"),
      List("4", "5", "6"),
      List("7", "8", "9"),
    )))
  }

  it should "fail if a line is invalid" in {
    val lines: fs2.Stream[TestIOTask, String] = fs2.Stream.emits(Vector(
      """1,2,3""",
      """4,"5,6""",
      """7,8,9""",
    ))

    val rows = ReadingInputStreams.streamCsv(lines, csv.defaultCSVFormat).compile.toVector.runEither(())

    assert(rows.left.map(_.getClass) === Left(classOf[IOException]))
    assert(rows.left.map(_.getMessage) === Left("""Invalid line '4,"5,6'"""))
  }

  it should "return the whole row even if the last element is empty" in {
    val lines: fs2.Stream[TestIOTask, String] = fs2.Stream.emits(Vector(
      """1,2,"3"""",
      """4,5,""""",
    ))

    val rows = ReadingInputStreams.streamCsv(lines, csv.defaultCSVFormat).compile.toVector.runEither(())

    assert(rows === Right(Vector(
      List("1", "2", "3"),
      List("4", "5", ""),
    )))
  }

}
