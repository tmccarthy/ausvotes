package au.id.tmm.ausvotes.lambdas.utils

import java.net.URL

import argonaut.Argonaut._
import argonaut.DecodeResult
import au.id.tmm.ausvotes.lambdas.utils.UrlCodec._
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class UrlCodecSpec extends ImprovedFlatSpec {

  "a URL" can "be encoded" in {
    assert(new URL("https://example.com").asJson === "https://example.com".asJson)
  }

  it can "be decoded" in {
    assert("https://example.com".asJson.as[URL] === DecodeResult.ok(new URL("https://example.com")))
  }

  it can "fail to decode" in {
    assert("invalid url".asJson.as[URL].result.left.get._1 === "no protocol: invalid url")
  }

}
