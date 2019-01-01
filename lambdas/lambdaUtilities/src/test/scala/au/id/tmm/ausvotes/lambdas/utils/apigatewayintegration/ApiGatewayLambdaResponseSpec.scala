package au.id.tmm.ausvotes.lambdas.utils.apigatewayintegration

import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.Json
import io.circe.syntax.EncoderOps

class ApiGatewayLambdaResponseSpec extends ImprovedFlatSpec {

  "a response" can "be encoded to json" in {
    assert(ApiGatewayLambdaResponse(200, Map.empty, Json.fromString("hello world")).asJson === Json.obj(
      "statusCode" -> 200.asJson,
      "headers" -> Json.obj(),
      "body" -> Json.fromString(""""hello world""""),
      "isBase64Encoded" -> false.asJson,
    ))
  }

}
