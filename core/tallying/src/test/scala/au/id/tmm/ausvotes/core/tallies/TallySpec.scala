package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.ausvotes.core.tallies.Tally.Ops._
import org.scalatest.FlatSpec
import cats.instances.int.catsKernelStdGroupForInt
import io.circe.Json
import io.circe.syntax.EncoderOps

class TallySpec extends FlatSpec {

  "a 1 teir tally" can "be added" in {
    val left = Tally("A" -> 1, "B" -> 2)
    val right = Tally("A" -> 2, "B" -> 3)

    assert(left + right === Tally("A" -> 3, "B" -> 5))
  }

  it should "count missing keys as 0 when adding" in {
    val left = Tally("A" -> 1, "B" -> 2)
    val right = Tally("B" -> 3)

    assert(left + right === Tally("A" -> 1, "B" -> 5))
  }

  it should "support result lookup directly" in {
    val tally = Tally("A" -> 2, "B" -> 8)

    assert(tally("A") === 2)
  }

  "a 4-times nested tally" can "be converted to a vector" in {
    val nestedTally = Tally[String, Tally[String, Tally[String, Tally[String, Int]]]](
      "there" -> Tally(
        "is" -> Tally(
          "a" -> Tally(
            "light" -> 1,
            "that" -> 2,
          ),
        ),
      ),
      "never" -> Tally(
        "goes" -> Tally(
          "out" -> Tally(
            "there" -> 3,
          ),
          "is" -> Tally(
            "a" -> 4,
          )
        ),
      ),
    )

    val expectedVector = Vector(
      ("there", "is",   "a",   "light", 1),
      ("there", "is",   "a",   "that",  2),
      ("never", "goes", "out", "there", 3),
      ("never", "goes", "is",  "a",     4),
    )

    assert(nestedTally.toVector === expectedVector)
  }

  "a 3-times nested tally" can "be converted to a vector" in {
    val nestedTally = Tally[String, Tally[String, Tally[String, Int]]](
      "there" -> Tally(
        "a" -> Tally(
          "light" -> 1,
          "that" -> 2,
        ),
      ),
      "never" -> Tally(
        "out" -> Tally(
          "there" -> 3,
        ),
        "is" -> Tally(
          "a" -> 4,
        )
      ),
    )

    val expectedVector = Vector(
      ("there", "a",   "light", 1),
      ("there", "a",   "that",  2),
      ("never", "out", "there", 3),
      ("never", "is",  "a",     4),
    )

    assert(nestedTally.toVector === expectedVector)
  }

  "a 2-times nested tally" can "be converted to a vector" in {
    val nestedTally = Tally[String, Tally[String, Int]](
      "a" -> Tally(
        "light" -> 1,
        "that" -> 2,
      ),
      "out" -> Tally(
        "there" -> 3,
      ),
      "is" -> Tally(
        "a" -> 4,
      )
    )

    val expectedVector = Vector(
      ("a",   "light", 1),
      ("a",   "that",  2),
      ("out", "there", 3),
      ("is",  "a",     4),
    )

    assert(nestedTally.toVector === expectedVector)
  }

  "an unnested tally" can "be converted to a vector" in {
    val tally = Tally[String, Int](
      "light" -> 1,
      "that" -> 2,
      "there" -> 3,
      "a" -> 4,
    )

    val expectedVector = Vector(
      ("light", 1),
      ("that",  2),
      ("there", 3),
      ("a",     4),
    )

    assert(tally.toVector === expectedVector)
  }

  it can "be encoded to json" in {
    val tally = Tally("A" -> 2, "B" -> 5, "C" -> 7)

    val expectedObject = Json.arr(
      Json.obj(
        "key" -> "A".asJson,
        "value" -> 2.asJson,
      ),
      Json.obj(
        "key" -> "B".asJson,
        "value" -> 5.asJson,
      ),
      Json.obj(
        "key" -> "C".asJson,
        "value" -> 7.asJson,
      ),
    )

    assert(tally.asJson === expectedObject)
  }

  it can "be decoded from json" in {
    val json = Json.arr(
      Json.obj(
        "key" -> "A".asJson,
        "value" -> 2.asJson,
      ),
      Json.obj(
        "key" -> "B".asJson,
        "value" -> 5.asJson,
      ),
      Json.obj(
        "key" -> "C".asJson,
        "value" -> 7.asJson,
      ),
    )

    val expectedTally = Tally("A" -> 2, "B" -> 5, "C" -> 7)

    assert(json.as[Tally1[String, Int]] === Right(expectedTally))
  }

}
