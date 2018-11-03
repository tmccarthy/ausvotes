package au.id.tmm.ausvotes.api.controllers

import argonaut.Argonaut._
import argonaut.Json
import au.id.tmm.ausvotes.api.config.Config
import au.id.tmm.ausvotes.api.errors.recount.RecountException
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.shared.aws.data.{ContentType, LambdaFunctionName, S3BucketName}
import au.id.tmm.ausvotes.shared.aws.testing.AwsTestData
import au.id.tmm.ausvotes.shared.aws.testing.AwsTestIoInstances._
import au.id.tmm.ausvotes.shared.aws.testing.datatraits.S3Interaction
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
                         ): (AwsTestData, Either[RecountException, Json]) = {
    new RecountController(config)
      .recount[AwsTestData.TestIO](recountRequest)
      .run(testData)
  }

  behaviour of "requesting a recount"

  it should "return a cached recount if one is present in s3" in {
    val request = RecountRequest(SenateElection.`2016`, State.SA, vacancies = 6, ineligibleCandidateAecIds = Set.empty)

    val cachedContentKey = RecountLocations.locationOfRecountFor(request)

    val testData = AwsTestData(
      s3Content = S3Interaction.InMemoryS3(
        config.recountDataBucket -> cachedContentKey -> ("{}", ContentType.APPLICATION_JSON),
      ),
    )

    val (testDataAfterwards, actualResult) = resultGiven(request, testData)

    assert(actualResult === Right(jEmptyObject))
    assert(testDataAfterwards.lambdaInvocations === List.empty)
  }

  it should "return a performed recount if one is not present in s3" in {
    val request = RecountRequest(SenateElection.`2016`, State.SA, vacancies = 6, ineligibleCandidateAecIds = Set.empty)

    val testData = AwsTestData(
      lambdaCallHandler = {
        case (config.recountFunction, payload) => Right("{}")
      },
      s3Content = S3Interaction.InMemoryS3.empty.addBucket(config.recountDataBucket),
    )

    val (testDataAfterwards, actualResult) = resultGiven(request, testData)

    assert(actualResult === Right(jEmptyObject))
    assert(testDataAfterwards.lambdaInvocations === List(
      config.recountFunction -> Some("""{"election":"2016","state":"SA","vacancies":6,"ineligibleCandidates":[]}""")
    ))
  }

}
