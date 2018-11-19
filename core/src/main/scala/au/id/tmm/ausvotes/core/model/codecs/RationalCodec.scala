package au.id.tmm.ausvotes.core.model.codecs

import argonaut.Argonaut._
import argonaut.{DecodeJson, DecodeResult, EncodeJson}
import spire.math.Rational

object RationalCodec {

  implicit val encodeRational: EncodeJson[Rational] = EncodeJson { r =>
    if (r.denominator.isOne) {
      jString(r.numerator.toString)
    } else {
      jString(s"${r.numerator}/${r.denominator}")
    }
  }

  implicit val decodeRational: DecodeJson[Rational] = DecodeJson { c =>
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
        case Right(rational) => DecodeResult.ok(rational)
        case Left(exception) => DecodeResult.fail(exception.getMessage, c.history)
      }
    }
  }

  // TODO put this into tmmUtils
  def asBigInt(string: String): Either[NumberFormatException, BigInt] = try Right(BigInt(string)) catch {
    case e: NumberFormatException => Left(e)
  }

}
