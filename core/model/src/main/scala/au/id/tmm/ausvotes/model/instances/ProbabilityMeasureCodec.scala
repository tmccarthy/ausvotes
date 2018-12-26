package au.id.tmm.ausvotes.model.instances

import au.id.tmm.ausvotes.model.instances.RationalCodec._
import au.id.tmm.utilities.probabilities.ProbabilityMeasure
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, DecodingFailure, Encoder, Json}
import spire.math.Rational

object ProbabilityMeasureCodec {

  implicit def encodeProbabilityMeasure[A : Encoder]: Encoder[ProbabilityMeasure[A]] =
    Encoder { probabilityMeasure =>
      Json.arr(
        probabilityMeasure.asMap.toList.map { case (possibility, probability) =>
          Json.obj(
            "probability" -> probability.asJson,
            "outcome" -> possibility.asJson,
          )
        }: _*
      )
    }

  implicit def decodeProbabilityMeasure[A : Decoder]: Decoder[ProbabilityMeasure[A]] = Decoder { c =>
    for {
      elements <- c.as[List[(A, Rational)]](Decoder.decodeList(decodeProbabilityMeasureElement))
      asMap = elements.toMap
      probabilityMeasure <- ProbabilityMeasure(asMap) match {
        case Right(probabilityMeasure) => Right(probabilityMeasure)
        case Left(constructionError) => Left(DecodingFailure(constructionError.toString, c.history))
      }
    } yield probabilityMeasure
  }

  private def decodeProbabilityMeasureElement[A : Decoder]: Decoder[(A, Rational)] = Decoder { c =>
    for {
      probability <- c.downField("probability").as[Rational]
      outcome <- c.downField("outcome").as[A]
    } yield outcome -> probability
  }

}
