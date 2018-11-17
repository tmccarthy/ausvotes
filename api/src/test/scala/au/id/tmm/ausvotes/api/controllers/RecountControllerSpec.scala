package au.id.tmm.ausvotes.api.controllers

import argonaut.Argonaut._
import argonaut.Json
import au.id.tmm.ausvotes.api.config.Config
import au.id.tmm.ausvotes.api.errors.recount.RecountException
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.shared.aws.data.{ContentType, LambdaFunctionName, S3BucketName}
import au.id.tmm.ausvotes.shared.aws.testing.AwsTestData
import au.id.tmm.ausvotes.shared.aws.testing.AwsTestData.AwsTestIO
import au.id.tmm.ausvotes.shared.aws.testing.testdata.LambdaTestData.LambdaInvocation
import au.id.tmm.ausvotes.shared.aws.testing.testdata.{LambdaTestData, S3TestData}
import au.id.tmm.ausvotes.shared.io.test.TestIO
import au.id.tmm.ausvotes.shared.io.test.TestIO._
import au.id.tmm.ausvotes.shared.recountresources.{RecountLocations, RecountRequest}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class RecountControllerSpec extends ImprovedFlatSpec {

  private val config: Config = Config(
    recountDataBucket = S3BucketName("recount"),
    recountFunction = LambdaFunctionName("recount"),
  )

  private def resultGiven(
                           recountRequest: RecountRequest,
                           testData: AwsTestData,
                         ): TestIO.Output[AwsTestData, RecountException, Json] = {
    new RecountController(config)
      .recount[AwsTestIO](recountRequest)
      .run(testData)
  }

  behaviour of "requesting a recount"

  it should "return a cached recount if one is present in s3" in {
    val request = RecountRequest(SenateElection.`2016`, State.SA, vacancies = 6, ineligibleCandidateAecIds = Set.empty)

    val cachedContentKey = RecountLocations.locationOfRecountFor(request)

    val testData = AwsTestData(
      s3TestData = S3TestData(
        s3Content = S3TestData.InMemoryS3(
          config.recountDataBucket -> cachedContentKey -> ("{}", ContentType.APPLICATION_JSON),
        ),
      ),
    )

    val output = resultGiven(request, testData)

    assert(output.result === Right(jEmptyObject))
    assert(output.testData.lambdaTestData.invocations === List.empty)
  }

  it should "return a performed recount if one is not present in s3" in {
    val request = RecountRequest(SenateElection.`2016`, State.SA, vacancies = 6, ineligibleCandidateAecIds = Set.empty)

    val testData = AwsTestData(
      lambdaTestData = LambdaTestData(
        handler = {
          case LambdaInvocation(config.recountFunction, payload) => Right("{}")
        },
      ),
      s3TestData = S3TestData(
        s3Content = S3TestData.InMemoryS3.empty.addBucket(config.recountDataBucket),
      ),
    )

    val output = resultGiven(request, testData)

    assert(output.result === Right(jEmptyObject))
    assert(output.testData.lambdaTestData.invocations === List(
      LambdaInvocation(config.recountFunction, Some("""{"election":"2016","state":"SA","vacancies":6,"ineligibleCandidates":[]}"""))
    ))
  }

}
