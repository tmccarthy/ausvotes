package au.id.tmm.ausvotes.lambdas.utils

import argonaut.Argonaut._
import argonaut._

final case class ApiGatewayLambdaResponse(
                                           statusCode: Int,
                                           headers: Map[String, String],
                                           body: Json,
                                         )

object ApiGatewayLambdaResponse {

  implicit val encodeResponse: EncodeJson[ApiGatewayLambdaResponse] = response => jObjectFields(
    "statusCode" -> response.statusCode.asJson,
    "headers" -> response.headers.asJson,
    "body" -> response.body.toString().asJson,
    "isBase64Encoded" -> false.asJson,
  )
}
