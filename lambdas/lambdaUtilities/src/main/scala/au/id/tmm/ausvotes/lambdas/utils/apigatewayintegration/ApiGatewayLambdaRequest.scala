package au.id.tmm.ausvotes.lambdas.utils.apigatewayintegration

import java.time.OffsetDateTime
import java.time.format.{DateTimeFormatter, DateTimeParseException}

import argonaut.{Argonaut, DecodeJson, DecodeResult}
import au.id.tmm.ausvotes.lambdas.utils.apigatewayintegration.ApiGatewayLambdaRequest.RequestContext

final case class ApiGatewayLambdaRequest(
                                          resource: String,
                                          path: String,
                                          httpMethod: String,
                                          headers: Map[String, String],
                                          queryStringParameters: Map[String, String],
                                          pathParameters: Map[String, String],
                                          stageVariables: Map[String, String],
                                          requestContext: RequestContext,
                                          body: Option[String],
                                          isBase64Encoded: Boolean,
                                        )

object ApiGatewayLambdaRequest {

  implicit val decoder: DecodeJson[ApiGatewayLambdaRequest] = c => for {
    resource <- c.downField("resource").as[String]
    path <- c.downField("path").as[String]
    httpMethod <- c.downField("httpMethod").as[String]
    headers <- c.downField("headers").as[Option[Map[String, String]]].map(_.getOrElse(Map.empty))
    queryStringParameters <- c.downField("queryStringParameters").as[Option[Map[String, String]]].map(_.getOrElse(Map.empty))
    pathParameters <- c.downField("pathParameters").as[Option[Map[String, String]]].map(_.getOrElse(Map.empty))
    stageVariables <- c.downField("stageVariables").as[Option[Map[String, String]]].map(_.getOrElse(Map.empty))
    requestContext <- c.downField("requestContext").as[RequestContext]
    body <- c.downField("body").as[Option[String]]
    isBase64Encoded <- c.downField("isBase64Encoded").as[Boolean]
  } yield ApiGatewayLambdaRequest(
    resource, path, httpMethod, headers, queryStringParameters, pathParameters, stageVariables, requestContext, body,
    isBase64Encoded,
  )

  final case class RequestContext(
                                   accountId: String,
                                   resourceId: String,
                                   stage: String,
                                   requestId: String,
                                   extendedRequestId: Option[String],
                                   requestTime: Option[OffsetDateTime],
                                   path: Option[String],
                                   protocol: Option[String],
                                   identity: RequestContext.Identity,
                                   resourcePath: String,
                                   httpMethod: String,
                                   apiId: String,
                                 )

  object RequestContext {

    private val requestTimeFormat = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss xxxx")

    private implicit val requestTimeDecoder: DecodeJson[OffsetDateTime] = c => c.as[String].flatMap { timestamp =>
      try {
        DecodeResult.ok(OffsetDateTime.parse(timestamp, requestTimeFormat))
      } catch {
        case e: DateTimeParseException => DecodeResult.fail(e.getMessage, c.history)
      }
    }

    implicit val decoder: DecodeJson[RequestContext] = Argonaut.jdecode12L(RequestContext.apply)(
      "accountId", "resourceId", "stage", "requestId", "extendedRequestId", "requestTime", "path", "protocol",
      "identity", "resourcePath", "httpMethod", "apiId",
    )

    final case class Identity(
                               cognitoIdentityPoolId: Option[String],
                               accountId: Option[String],
                               cognitoIdentityId: Option[String],
                               caller: Option[String],
                               apiKey: Option[String],
                               sourceIp: String,
                               accessKey: Option[String],
                               cognitoAuthenticationType: Option[String],
                               cognitoAuthenticationProvider: Option[String],
                               userArn: Option[String],
                               userAgent: String,
                               user: Option[String],
                             )

    object Identity {
      implicit val decoder: DecodeJson[Identity] = Argonaut.jdecode12L(Identity.apply)(
        "cognitoIdentityPoolId", "accountId", "cognitoIdentityId", "caller", "apiKey", "sourceIp", "accessKey",
        "cognitoAuthenticationType", "cognitoAuthenticationProvider", "userArn", "userAgent", "user",
      )
    }
  }
}
