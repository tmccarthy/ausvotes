package au.id.tmm.ausvotes.model.instances

import io.circe.{Decoder, DecodingFailure, Encoder, Json}
import spire.math.Rational

object RationalCodec {

  implicit val encodeRational: Encoder[Rational] = Encoder { r =>
    if (r.denominator.isOne) {
      Json.fromString(r.numerator.toString)
    } else {
      Json.fromString(s"${r.numerator}/${r.denominator}")
    }
  }

  implicit val decodeRational: Decoder[Rational] = Decoder { c =>
    c.as[String].flatMap { rawString =>
      val parts = rawString.split('/').toList

      val errorOrRational = parts match {
        case singlePart :: Nil => asBigInt(singlePart).map(Rational(_))
        case numeratorPart :: denominatorPart :: Nil =>
          for {
            numerator <- asBigInt(numeratorPart)
            denominator <- asBigInt(denominatorPart)
          } yield Rational(numerator, denominator)
        case _ => Left(new Exception(s"Invalid rational $rawString"))
      }

      errorOrRational match {
        case Right(rational) => Right(rational)
        case Left(exception) => Left(DecodingFailure(exception.getMessage, c.history))
      }
    }
  }

  // TODO put this into tmmUtils
  def asBigInt(string: String): Either[NumberFormatException, BigInt] = try Right(BigInt(string)) catch {
    case e: NumberFormatException => Left(e)
  }

}
