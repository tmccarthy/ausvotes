package au.id.tmm.ausvotes.core.tallies

import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.Json
import io.circe.syntax.EncoderOps

class Tally2Spec extends ImprovedFlatSpec {

  "a doubly-tiered tally" can "be added" in {
    val left = Tally2("!" -> Tally1("A" -> 1, "B" -> 2), "@" -> Tally1("C" -> 4d))
    val right = Tally2("!" -> Tally1("A" -> 2, "B" -> 3), "@" -> Tally1("C" -> 1d))

    assert(left + right === Tally2("!" -> Tally1("A" -> 3, "B" -> 5), "@" -> Tally1("C" -> 5d)))
  }

  it should "count missing keys as empty when adding" in {
    val left = Tally2("!" -> Tally1("A" -> 1, "B" -> 2), "@" -> Tally1("C" -> 4d))
    val right = Tally2("!" -> Tally1("A" -> 2, "B" -> 3))

    assert(left + right === Tally2("!" -> Tally1("A" -> 3, "B" -> 5), "@" -> Tally1("C" -> 4d)))
  }

  it should "support lookup of the tally for a particular tier" in {
    val tally = Tally2("!" -> Tally1("A" -> 1, "B" -> 2), "@" -> Tally1("C" -> 4d))

    assert(tally("!") === Tally1("A" -> 1, "B" -> 2))
  }

  it can "be encoded to json" in {
    val tally = Tally2(
      "!" -> Tally1(
        "A" -> 1,
        "B" -> 2,
      ),
      "@" -> Tally1(
        "C" -> 4d,
      ),
    )

    val expectedJson = Json.arr(
      Json.obj(
        "key" -> "!".asJson,
        "value" -> Json.arr(
          Json.obj(
            "key" -> "A".asJson,
            "value" -> 1.asJson,
          ),
          Json.obj(
            "key" -> "B".asJson,
            "value" -> 2.asJson,
          ),
        )
      ),
      Json.obj(
        "key" -> "@".asJson,
        "value" -> Json.arr(
          Json.obj(
            "key" -> "C".asJson,
            "value" -> 4.asJson,
          ),
        ),
      ),
    )

    assert(tally.asJson === expectedJson)
  }

  it can "be decoded from json" in {
    val json = Json.arr(
      Json.obj(
        "key" -> "!".asJson,
        "value" -> Json.arr(
          Json.obj(
            "key" -> "A".asJson,
            "value" -> 1.asJson,
          ),
          Json.obj(
            "key" -> "B".asJson,
            "value" -> 2.asJson,
          ),
        )
      ),
      Json.obj(
        "key" -> "@".asJson,
        "value" -> Json.arr(
          Json.obj(
            "key" -> "C".asJson,
            "value" -> 4.asJson,
          ),
        ),
      ),
    )

    val expectedTally = Tally2(
      "!" -> Tally1(
        "A" -> 1,
        "B" -> 2,
      ),
      "@" -> Tally1(
        "C" -> 4d,
      ),
    )

    assert(json.as[Tally2[String, String]] === Right(expectedTally))
  }

}
