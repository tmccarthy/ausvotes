package au.id.tmm.ausvotes.lambdas.utils.apigatewayintegration

import io.circe.syntax.EncoderOps
import io.circe.{Encoder, Json}

final case class ApiGatewayLambdaResponse(
                                           statusCode: Int,
                                           headers: Map[String, String],
                                           body: Json,
                                         )

object ApiGatewayLambdaResponse {

  implicit val encodeResponse: Encoder[ApiGatewayLambdaResponse] = response => Json.obj(
    "statusCode" -> response.statusCode.asJson,
    "headers" -> response.headers.asJson,
    "body" -> response.body.toString().asJson,
    "isBase64Encoded" -> false.asJson,
  )
}
