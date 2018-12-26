package au.id.tmm.ausvotes.model.instances

import au.id.tmm.ausvotes.model.instances.ProbabilityMeasureCodec._
import au.id.tmm.utilities.probabilities.ProbabilityMeasure
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.Json
import io.circe.syntax.EncoderOps
import spire.math.Rational

class ProbabilityMeasureCodecSpec extends ImprovedFlatSpec {

  "The probability measure codec" can "encode a probability measure" in {
    val probabilityMeasure = ProbabilityMeasure(
      "hello" -> Rational(1, 3),
      "world" -> Rational(2, 3),
    ).getOrElse(fail())

    val expectedJson = Json.arr(
      Json.obj(
        "probability" -> "2/3".asJson,
        "outcome" -> "world".asJson,
      ),
      Json.obj(
        "probability" -> "1/3".asJson,
        "outcome" -> "hello".asJson,
      ),
    )

    assert(probabilityMeasure.asJson === expectedJson)
  }

  it can "decode a probability measure" in {
    val json = Json.arr(
      Json.obj(
        "probability" -> "2/3".asJson,
        "outcome" -> "world".asJson,
      ),
      Json.obj(
        "probability" -> "1/3".asJson,
        "outcome" -> "hello".asJson,
      ),
    )

    val expectedProbabilityMeasure = ProbabilityMeasure(
      "hello" -> Rational(1, 3),
      "world" -> Rational(2, 3),
    ).getOrElse(fail())

    assert(json.as[ProbabilityMeasure[String]] === Right(expectedProbabilityMeasure))
  }

  it should "fail to decode an invalid probability measure" in {
    val json = Json.arr(
      Json.obj(
        "probability" -> "2/3".asJson,
        "outcome" -> "world".asJson,
      ),
    )

    assert(json.as[ProbabilityMeasure[String]].left.map(_.message) === Left("ProbabilitiesDontSumToOne"))
  }

}
