package au.id.tmm.ausvotes.lambdas.utils

import java.time.format.{DateTimeFormatter, DateTimeParseException}
import java.time.{Instant, OffsetDateTime}

import argonaut.{DecodeJson, DecodeResult}

object DateTimeCodecs {

  def offsetDateTimeDecoder(formatter: DateTimeFormatter): DecodeJson[OffsetDateTime] = c => c.as[String]
    .flatMap { timestamp =>
      try {
        DecodeResult.ok(OffsetDateTime.parse(timestamp, formatter))
      } catch {
        case e: DateTimeParseException => DecodeResult.fail(e.getMessage, c.history)
      }
    }

  def instantDecoder(formatter: DateTimeFormatter): DecodeJson[Instant] = c => c.as[String]
    .flatMap { timestamp =>
      try {
        DecodeResult.ok(formatter.parse(timestamp, Instant.from _))
      } catch {
        case e: DateTimeParseException => DecodeResult.fail(e.getMessage, c.history)
      }
    }

  implicit val instantDecoder: DecodeJson[Instant] = instantDecoder(formatter = DateTimeFormatter.ISO_INSTANT)

}
