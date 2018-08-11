package au.id.tmm.ausvotes.lambdas.utils

import java.time.{OffsetDateTime, ZoneOffset}

import argonaut.Parse
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class LambdaRequestSpec extends ImprovedFlatSpec {

  "a request" can "be decoded" in {
    val requestJson =
      """{
        |  "resource": "/recount/{election}/{state}",
        |  "path": "/recount/2016/VIC",
        |  "httpMethod": "GET",
        |  "headers": {
        |    "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
        |    "Accept-Encoding": "gzip, deflate, br",
        |    "Accept-Language": "en-AU,en-US;q=0.7,en;q=0.3",
        |    "CloudFront-Forwarded-Proto": "https"
        |  },
        |  "queryStringParameters": {
        |    "vacancies": "12",
        |    "ineligibleCandidates": "123,456,789"
        |  },
        |  "pathParameters": {
        |    "election": "2016",
        |    "state": "VIC"
        |  },
        |  "stageVariables": null,
        |  "requestContext": {
        |    "resourceId": "b187k5",
        |    "resourcePath": "/recount/{election}/{state}",
        |    "httpMethod": "GET",
        |    "extendedRequestId": "LJNjAEPySwMFtPQ=",
        |    "requestTime": "05/Aug/2018:09:26:56 +0000",
        |    "path": "/prod/recount/2016/VIC",
        |    "accountId": "327455522484",
        |    "protocol": "HTTP/1.1",
        |    "stage": "prod",
        |    "requestTimeEpoch": 1533461216033,
        |    "requestId": "b216f917-9891-11e8-b41f-f7a42232c9f4",
        |    "identity": {
        |      "cognitoIdentityPoolId": null,
        |      "accountId": null,
        |      "cognitoIdentityId": null,
        |      "caller": null,
        |      "sourceIp": "10.0.0.1",
        |      "accessKey": null,
        |      "cognitoAuthenticationType": null,
        |      "cognitoAuthenticationProvider": null,
        |      "userArn": null,
        |      "userAgent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:57.0) Gecko/20100101 Firefox/57.0",
        |      "user": null
        |    },
        |    "apiId": "jr1bxq5igk"
        |  },
        |  "body": null,
        |  "isBase64Encoded": false
        |}
        |""".stripMargin

    val expectedRequest = LambdaRequest(
      resource = "/recount/{election}/{state}",
      path = "/recount/2016/VIC",
      httpMethod = "GET",
      headers = Map(
        "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
        "Accept-Encoding" -> "gzip, deflate, br",
        "Accept-Language" -> "en-AU,en-US;q=0.7,en;q=0.3",
        "CloudFront-Forwarded-Proto" -> "https",
      ),
      queryStringParameters = Map(
        "vacancies" -> "12",
        "ineligibleCandidates" -> "123,456,789",
      ),
      pathParameters = Map(
        "election" -> "2016",
        "state" -> "VIC",
      ),
      stageVariables = Map.empty,
      requestContext = LambdaRequest.RequestContext(
        resourceId = "b187k5",
        resourcePath = "/recount/{election}/{state}",
        httpMethod = "GET",
        extendedRequestId = Some("LJNjAEPySwMFtPQ="),
        requestTime = OffsetDateTime.of(2018, 8, 5, 9, 26, 56, 0, ZoneOffset.ofHours(0)),
        path = Some("/prod/recount/2016/VIC"),
        accountId = "327455522484",
        protocol = Some("HTTP/1.1"),
        stage = "prod",
        requestId = "b216f917-9891-11e8-b41f-f7a42232c9f4",
        identity = LambdaRequest.RequestContext.Identity(
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
      body = None,
      isBase64Encoded = false,
    )

    assert(Parse.decodeEither[LambdaRequest](requestJson) === Right(expectedRequest))
  }

}
