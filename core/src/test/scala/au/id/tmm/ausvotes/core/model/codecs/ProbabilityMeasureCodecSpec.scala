package au.id.tmm.ausvotes.core.model.codecs

import argonaut.Argonaut._
import argonaut.DecodeResult
import au.id.tmm.utilities.probabilities.ProbabilityMeasure
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import spire.math.Rational

class ProbabilityMeasureCodecSpec extends ImprovedFlatSpec {

  import ProbabilityMeasureCodec._

  "The probability measure codec" can "encode a probability measure" in {
    val probabilityMeasure = ProbabilityMeasure(
      "hello" -> Rational(1, 3),
      "world" -> Rational(2, 3),
    ).getOrElse(fail())

    val expectedJson = jArrayElements(
      jObjectFields(
        "probability" -> "2/3".asJson,
        "outcome" -> "world".asJson,
      ),
      jObjectFields(
        "probability" -> "1/3".asJson,
        "outcome" -> "hello".asJson,
      ),
    )

    assert(probabilityMeasure.asJson === expectedJson)
  }

  it can "decode a probability measure" in {
    val json = jArrayElements(
      jObjectFields(
        "probability" -> "2/3".asJson,
        "outcome" -> "world".asJson,
      ),
      jObjectFields(
        "probability" -> "1/3".asJson,
        "outcome" -> "hello".asJson,
      ),
    )

    val expectedProbabilityMeasure = ProbabilityMeasure(
      "hello" -> Rational(1, 3),
      "world" -> Rational(2, 3),
    ).getOrElse(fail())

    assert(json.as[ProbabilityMeasure[String]] === DecodeResult.ok(expectedProbabilityMeasure))
  }

  it should "fail to decode an invalid probability measure" in {
    val json = jArrayElements(
      jObjectFields(
        "probability" -> "2/3".asJson,
        "outcome" -> "world".asJson,
      ),
    )

    assert(json.as[ProbabilityMeasure[String]].message === Some("ProbabilitiesDontSumToOne"))
  }

}
