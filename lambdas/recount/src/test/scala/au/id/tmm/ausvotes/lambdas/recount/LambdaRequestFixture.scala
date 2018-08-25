package au.id.tmm.ausvotes.lambdas.recount

import java.time.{OffsetDateTime, ZoneOffset}

import au.id.tmm.ausvotes.lambdas.utils.apigatewayintegration.ApiGatewayLambdaRequest

object LambdaRequestFixture {

  def lambdaRequest(
                     resource: String = "/recount/{election}/{state}",
                     path: String = "/recount/2016/VIC",
                     httpMethod: String = "GET",
                     headers: Map[String, String] = Map(
                       "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
                       "Accept-Encoding" -> "gzip, deflate, br",
                       "Accept-Language" -> "en-AU,en-US;q=0.7,en;q=0.3",
                       "CloudFront-Forwarded-Proto" -> "https",
                     ),
                     queryStringParameters: Map[String, String] = Map(
                       "vacancies" -> "12",
                       "ineligibleCandidates" -> "123,456,789",
                     ),
                     pathParameters: Map[String, String] = Map(
                       "election" -> "2016",
                       "state" -> "VIC",
                     ),
                     stageVariables: Map[String, String] = Map.empty,
                     requestContext: ApiGatewayLambdaRequest.RequestContext = ApiGatewayLambdaRequest.RequestContext(
                       resourceId = "b187k5",
                       resourcePath = "/recount/{election}/{state}",
                       httpMethod = "GET",
                       extendedRequestId = Some("LJNjAEPySwMFtPQ="),
                       requestTime = Some(OffsetDateTime.of(2018, 8, 5, 9, 26, 56, 0, ZoneOffset.ofHours(0))),
                       path = Some("/prod/recount/2016/VIC"),
                       accountId = "327455522484",
                       protocol = Some("HTTP/1.1"),
                       stage = "prod",
                       requestId = "b216f917-9891-11e8-b41f-f7a42232c9f4",
                       identity = ApiGatewayLambdaRequest.RequestContext.Identity(
                         cognitoIdentityPoolId = None,
                         accountId = None,
                         cognitoIdentityId = None,
                         caller = None,
                         apiKey = None,
                         sourceIp = "10.0.0.1",
                         accessKey = None,
                         cognitoAuthenticationType = None,
                         cognitoAuthenticationProvider = None,
                         userArn = None,
                         userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:57.0) Gecko/20100101 Firefox/57.0",
                         user = None,
                       ),
                       apiId = "jr1bxq5igk"
                     ),
                     body: Option[String] = None,
                     isBase64Encoded: Boolean = false,
                   ): ApiGatewayLambdaRequest = ApiGatewayLambdaRequest(
    resource, path, httpMethod, headers, queryStringParameters, pathParameters, stageVariables, requestContext, body,
    isBase64Encoded
  )

}
