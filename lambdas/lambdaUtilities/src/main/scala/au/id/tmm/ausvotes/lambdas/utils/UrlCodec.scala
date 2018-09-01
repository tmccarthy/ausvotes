package au.id.tmm.ausvotes.lambdas.utils

import java.net.{MalformedURLException, URL}

import argonaut.Argonaut._
import argonaut._

object UrlCodec {

  implicit val encodeUrl: EncodeJson[URL] = url => url.toString.asJson

  implicit val decodeUrl: DecodeJson[URL] = c => c.as[String].flatMap { string =>
    try {
      DecodeResult.ok(new URL(string))
    } catch {
      case e: MalformedURLException => DecodeResult.fail(e.getMessage, c.history)
    }
  }

}
