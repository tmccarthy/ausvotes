package au.id.tmm.ausvotes.lambdas.recountenqueue

import argonaut.Argonaut._
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.lambdas.utils.apigatewayintegration.{ApiGatewayLambdaRequest, ApiGatewayLambdaResponse}
import au.id.tmm.ausvotes.shared.aws.data.{ContentType, S3BucketName, S3ObjectKey}
import au.id.tmm.ausvotes.shared.aws.testing.AwsTestData
import au.id.tmm.ausvotes.shared.aws.testing.AwsTestData.AwsTestIO
import au.id.tmm.ausvotes.shared.aws.testing.testdata.S3TestData.InMemoryS3
import au.id.tmm.ausvotes.shared.aws.testing.testdata.{S3TestData, SnsTestData}
import au.id.tmm.ausvotes.shared.io.test.testdata.EnvVarTestData
import au.id.tmm.ausvotes.shared.io.test.{BasicTestData, TestIO}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class RecountEnqueueLambdaSpec extends ImprovedFlatSpec {

  private val logicUnderTest: ApiGatewayLambdaRequest => AwsTestIO[RecountEnqueueLambda.Error, ApiGatewayLambdaResponse] =
    RecountEnqueueLambda.recountEnqueueLogic[AwsTestIO]

  private val recountRequestQueueTopicArn = "recountRequestArn"
  private val recountDataBucket = "recountDataBucket"

  private val greenPathTestData: AwsTestData = AwsTestData(
    basicTestData = BasicTestData(
      envVarTestData = EnvVarTestData(
        envVars = Map(
          "RECOUNT_REQUEST_QUEUE" -> recountRequestQueueTopicArn,
          "RECOUNT_DATA_BUCKET" -> recountDataBucket,
          "AWS_DEFAULT_REGION" -> "ap-southeast-2",
        ),
      ),
    ),
    s3TestData = S3TestData(
      s3Content = InMemoryS3(Set(
        InMemoryS3.Bucket(S3BucketName(recountDataBucket), Set.empty),
      )),
    ),
    snsTestData = SnsTestData(
      snsMessagesPerTopic = Map.empty.withDefaultValue(Nil),
    ),
  )

  private def lambdaRequestFor(
                                election: Option[SenateElection],
                                state: Option[State],
                                numVacancies: Option[Int] = None,
                                ineligibleCandidates: Option[String] = None,
                              ): ApiGatewayLambdaRequest = ApiGatewayLambdaRequest(
    resource = "",
    path = "",
    httpMethod = "",
    headers = Map.empty,
    queryStringParameters = buildParamsMap(
      "vacancies" -> numVacancies,
      "ineligibleCandidates" -> ineligibleCandidates,
    ),
    pathParameters = buildParamsMap(
      "election" -> election.map(_.id),
      "state" -> state.map(_.abbreviation),
    ),
    stageVariables = Map.empty,
    requestContext = ApiGatewayLambdaRequest.RequestContext(
      accountId = "",
      resourceId = "",
      stage = "",
      requestId = "",
      extendedRequestId = None,
      requestTime = None,
      path = None,
      protocol = None,
      identity = ApiGatewayLambdaRequest.RequestContext.Identity(
        cognitoIdentityPoolId = None,
        accountId = None,
        cognitoIdentityId = None,
        caller = None,
        apiKey = None,
        sourceIp = "",
        accessKey = None,
        cognitoAuthenticationType = None,
        cognitoAuthenticationProvider = None,
        userArn = None,
        userAgent = "",
        user = None,
      ),
      resourcePath = "",
      httpMethod = "",
      apiId = "",
    ),
    body = None,
    isBase64Encoded = false,
  )

  private def buildParamsMap(pairs: (String, Option[Any])*): Map[String, String] = pairs
    .collect {
      case (key, Some(value)) => key -> value.toString
    }
    .toMap

  "the recount lambda" should "send an recount request when none has previously been calculated" in {
    val logic = logicUnderTest(lambdaRequestFor(Some(SenateElection.`2016`), Some(State.SA)))

    val TestIO.Output(outputData, response) = logic.run(greenPathTestData)

    assert(outputData.snsTestData.snsMessagesPerTopic(recountRequestQueueTopicArn) === List("""{"election":"2016","state":"SA","vacancies":12,"ineligibleCandidates":[]}"""))
  }

  it should "respond successfully to a valid request" in {
    val logic = logicUnderTest(lambdaRequestFor(Some(SenateElection.`2016`), Some(State.SA), ineligibleCandidates = Some("123,456")))

    val TestIO.Output(outputData, response) = logic.run(greenPathTestData)

    val expectedResponse = ApiGatewayLambdaResponse(
      statusCode = 202,
      headers = Map.empty,
      body = jObjectFields(
        "recountRequest" -> jObjectFields(
          "election" -> jString("2016"),
          "state" -> jString("SA"),
          "vacancies" -> jNumber(12),
          "ineligibleCandidates" -> jArray(List(
            jString("123"),
            jString("456"),
          )),
        ),
        "recountLocation" -> jString(s"https://s3-ap-southeast-2.amazonaws.com/$recountDataBucket/recounts/2016/SA/12-vacancies/123-456-ineligible/result.json"),
      )
    )

    assert(response === Right(expectedResponse))
  }

  it should "not send a recount request if one has been previously calculated" in {
    val logic = logicUnderTest(lambdaRequestFor(Some(SenateElection.`2016`), Some(State.SA)))

    val testData = greenPathTestData.copy(
      s3TestData = greenPathTestData.s3TestData.copy(
        s3Content = InMemoryS3(Set(
          InMemoryS3.Bucket(S3BucketName(recountDataBucket), Set(
            InMemoryS3.S3Object(
              S3ObjectKey("recounts") / "2016" / "SA" / "12-vacancies" / "none-ineligible" / "result.json",
              "",
              ContentType.APPLICATION_JSON,
            )
          )),
        )),
      ),
    )

    val TestIO.Output(outputData, response) = logic.run(testData)

    assert(response.map(_.statusCode) === Right(202))
    assert(outputData.snsTestData.snsMessagesPerTopic(recountRequestQueueTopicArn) === Nil)
  }

}
