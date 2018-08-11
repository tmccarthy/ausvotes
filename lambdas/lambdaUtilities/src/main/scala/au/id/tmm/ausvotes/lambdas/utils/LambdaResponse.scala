package au.id.tmm.ausvotes.lambdas.utils

import argonaut.Argonaut._
import argonaut._

final case class LambdaResponse(
                           statusCode: Int,
                           headers: Map[String, String],
                           body: Json,
                         )

object LambdaResponse {

  implicit val encodeResponse: EncodeJson[LambdaResponse] = response => jObjectFields(
    "statusCode" -> response.statusCode.asJson,
    "headers" -> response.headers.asJson,
    "body" -> response.body.toString().asJson,
    "isBase64Encoded" -> false.asJson,
  )
}
