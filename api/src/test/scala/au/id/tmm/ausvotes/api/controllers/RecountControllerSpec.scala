package au.id.tmm.ausvotes.api.controllers

import au.id.tmm.ausvotes.api.config.Config
import au.id.tmm.ausvotes.api.errors.recount.RecountException
import au.id.tmm.ausvotes.api.model.recount.RecountApiRequest
import au.id.tmm.ausvotes.core.fixtures.CandidateFixture
import au.id.tmm.ausvotes.model.federal.senate.SenateElection
import au.id.tmm.ausvotes.shared.aws.data.{ContentType, LambdaFunctionName, S3BucketName}
import au.id.tmm.ausvotes.shared.aws.testing.AwsTestData
import au.id.tmm.ausvotes.shared.aws.testing.AwsTestData.AwsTestIO
import au.id.tmm.ausvotes.shared.aws.testing.testdata.LambdaTestData.LambdaInvocation
import au.id.tmm.ausvotes.shared.aws.testing.testdata.{LambdaTestData, S3TestData}
import au.id.tmm.ausvotes.shared.io.test.TestIO._
import au.id.tmm.ausvotes.shared.recountresources.{RecountLocations, RecountRequest}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.Json

class RecountControllerSpec extends ImprovedFlatSpec {

  private val config: Config = Config(
    recountDataBucket = S3BucketName("recount"),
    recountFunction = LambdaFunctionName("recount"),
    basePath = List.empty,
  )

  private def resultGiven(
                           recountRequest: RecountApiRequest,
                           testData: AwsTestData,
                         ): (AwsTestData, Either[RecountException, Json]) = {
    new RecountController(config)
      .recount[AwsTestIO](recountRequest)
      .run(testData)
  }

  behaviour of "requesting a recount"

  private val election = SenateElection.`2016`.electionForState(State.ACT).get

  it should "return a cached recount if one is present in s3" in {
    val request = RecountApiRequest(election, numVacancies = Some(2), ineligibleCandidates = None, doRounding = None)

    val cachedContentKey = RecountLocations.locationOfRecountFor(
      RecountRequest(
        request.election,
        request.numVacancies.get,
        Set(CandidateFixture.ACT.katyGallagher.candidateDetails.id),
        request.doRounding.getOrElse(true),
      )
    )

    val testData = AwsTestData(
      s3TestData = S3TestData(
        s3Content = S3TestData.InMemoryS3(
          config.recountDataBucket -> cachedContentKey -> ("{}", ContentType.APPLICATION_JSON),
        ),
      ),
    )

    val output = resultGiven(request, testData)

    assert(output._2 === Right(Json.obj()))
    assert(output._1.lambdaTestData.invocations === List.empty)
  }

  it should "return a performed recount if one is not present in s3" in {
    val request = RecountApiRequest(election, numVacancies = Some(2), ineligibleCandidates = None, doRounding = None)

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

    assert(output._2 === Right(Json.obj()))
    assert(output._1.lambdaTestData.invocations === List(
      LambdaInvocation(config.recountFunction, Some("""{"election":{"election":"2016","state":"ACT"},"vacancies":2,"ineligibleCandidates":[28147],"doRounding":true}"""))
    ))
  }

}
