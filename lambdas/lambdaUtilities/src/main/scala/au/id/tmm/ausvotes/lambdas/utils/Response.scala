package au.id.tmm.ausvotes.lambdas.utils

import argonaut.Argonaut._
import argonaut._

final case class Response(
                           statusCode: Int,
                           headers: Map[String, String],
                           body: Json,
                         )

object Response {

  implicit val encodeResponse: EncodeJson[Response] = response => jObjectFields(
    "statusCode" -> response.statusCode.asJson,
    "headers" -> response.headers.asJson,
    "body" -> response.body.toString().asJson,
    "isBase64Encoded" -> false.asJson,
  )
}
