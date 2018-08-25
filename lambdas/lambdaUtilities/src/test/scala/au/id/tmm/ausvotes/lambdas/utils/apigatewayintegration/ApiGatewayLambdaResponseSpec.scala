package au.id.tmm.ausvotes.lambdas.utils.apigatewayintegration

import argonaut.Argonaut._
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ApiGatewayLambdaResponseSpec extends ImprovedFlatSpec {

  "a response" can "be encoded to json" in {
    assert(ApiGatewayLambdaResponse(200, Map.empty, jString("hello world")).asJson === jObjectFields(
      "statusCode" -> 200.asJson,
      "headers" -> jEmptyObject,
      "body" -> jString(""""hello world""""),
      "isBase64Encoded" -> false.asJson,
    ))
  }

}
