package au.id.tmm.ausvotes.lambdas.utils

import argonaut.Argonaut._
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ResponseSpec extends ImprovedFlatSpec {

  "a response" can "be encoded to json" in {
    assert(Response(200, Map.empty, jString("hello world")).asJson === jObjectFields(
      "statusCode" -> 200.asJson,
      "headers" -> jEmptyObject,
      "body" -> jString(""""hello world""""),
      "isBase64Encoded" -> false.asJson,
    ))
  }

}
