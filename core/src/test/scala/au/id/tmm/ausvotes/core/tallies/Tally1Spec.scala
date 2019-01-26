package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.Json
import io.circe.syntax.EncoderOps

class Tally1Spec extends ImprovedFlatSpec {

  "a 1 teir tally" can "be added" in {
    val left = Tally1("A" -> 1, "B" -> 2)
    val right = Tally1("A" -> 2, "B" -> 3)

    assert(left + right === Tally1("A" -> 3, "B" -> 5))
  }

  it should "count missing keys as 0 when adding" in {
    val left = Tally1("A" -> 1, "B" -> 2)
    val right = Tally1("B" -> 3d)

    assert(left + right === Tally1("A" -> 1, "B" -> 5))
  }

  it should "support result lookup directly" in {
    val tally = Tally1("A" -> 2, "B" -> 8)

    assert(tally("A") === Tally0(2))
  }

  it can "be divided by a double" in {
    val tally = Tally1("A" -> 2, "B" -> 4)

    assert(tally / 2 === Tally1("A" -> 1, "B" -> 2))
  }

  it can "be divided by another tally" in {
    val left = Tally1("A" -> 2, "B" -> 20)
    val right = Tally1("A" -> 2, "B" -> 5, "C" -> 7)

    val expected = Tally1("A" -> 1, "B" -> 4)

    assert(left / right === expected)
  }

  it can "not be divided by another tally that is missing a key in the first tally" in {
    val left = Tally1("A" -> 2, "B" -> 5, "C" -> 7)
    val right = Tally1("A" -> 2, "B" -> 20)

    intercept[NoSuchElementException] {
      left / right
    }
  }

  it can "be converted to a stream" in {
    val tally = Tally1("A" -> 2, "B" -> 5, "C" -> 7)

    val expected = Vector(
      ("A", 2),
      ("B", 5),
      ("C", 7),
    )

    val actual = tally.asStream.toVector

    assert(expected === actual)
  }

  it can "be accumulated into a Tally0" in {
    val tally = Tally1("A" -> 2, "B" -> 5, "C" -> 7)

    assert(tally.accumulated === Tally0(2 + 5 + 7))
  }

  it can "be encoded to json" in {
    val tally = Tally1("A" -> 2, "B" -> 5, "C" -> 7)

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

    val expectedTally = Tally1("A" -> 2, "B" -> 5, "C" -> 7)

    assert(json.as[Tally1[String]] === Right(expectedTally))
  }

}
