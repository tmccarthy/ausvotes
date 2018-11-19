package au.id.tmm.ausvotes.core.model.codecs

import argonaut.Argonaut._
import argonaut.{DecodeJson, DecodeResult, EncodeJson}
import au.id.tmm.utilities.probabilities.ProbabilityMeasure
import spire.math.Rational

object ProbabilityMeasureCodec {

  import RationalCodec._

  implicit def encodeProbabilityMeasure[A : EncodeJson]: EncodeJson[ProbabilityMeasure[A]] =
    EncodeJson { probabilityMeasure =>
      jArrayElements(
        probabilityMeasure.asMap.toList.map { case (possibility, probability) =>
          jObjectFields(
            "probability" -> probability.asJson,
            "outcome" -> possibility.asJson,
          )
        }: _*
      )
    }

  implicit def decodeProbabilityMeasure[A : DecodeJson]: DecodeJson[ProbabilityMeasure[A]] = DecodeJson { c =>
    for {
      elements <- c.as[List[(A, Rational)]] (ListDecodeJson(decodeProbabilityMeasureElement))
      asMap = elements.toMap
      probabilityMeasure <- ProbabilityMeasure(asMap) match {
        case Right(probabilityMeasure) => DecodeResult.ok(probabilityMeasure)
        case Left(constructionError) => DecodeResult.fail(constructionError.toString, c.history)
      }
    } yield probabilityMeasure
  }

  private def decodeProbabilityMeasureElement[A : DecodeJson]: DecodeJson[(A, Rational)] = DecodeJson { c =>
    for {
      probability <- c.downField("probability").as[Rational]
      outcome <- c.downField("outcome").as[A]
    } yield outcome -> probability
  }

}
