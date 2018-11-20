package au.id.tmm.ausvotes.api.controllers

import argonaut.Argonaut._
import argonaut.Json
import au.id.tmm.ausvotes.api.config.Config
import au.id.tmm.ausvotes.api.controllers.RecountControllerSpec.{EntitiesTestIO, TestData}
import au.id.tmm.ausvotes.api.errors.recount.RecountException
import au.id.tmm.ausvotes.api.model.recount.RecountApiRequest
import au.id.tmm.ausvotes.core.fixtures.CandidateFixture
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.shared.aws.data.{ContentType, LambdaFunctionName, S3BucketName}
import au.id.tmm.ausvotes.shared.aws.testing.AwsTestData
import au.id.tmm.ausvotes.shared.aws.testing.testdata.LambdaTestData.LambdaInvocation
import au.id.tmm.ausvotes.shared.aws.testing.testdata.{LambdaTestData, S3TestData}
import au.id.tmm.ausvotes.shared.io.test.TestIO
import au.id.tmm.ausvotes.shared.io.test.TestIO._
import au.id.tmm.ausvotes.shared.recountresources.entities.testing.EntitiesTestData
import au.id.tmm.ausvotes.shared.recountresources.{RecountLocations, RecountRequest, RecountResult}
import au.id.tmm.countstv.model.CandidateStatuses
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.probabilities.ProbabilityMeasure
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class RecountControllerSpec extends ImprovedFlatSpec {

  private val config: Config = Config(
    recountDataBucket = S3BucketName("recount"),
    recountFunction = LambdaFunctionName("recount"),
  )

  private def resultGiven(
                           recountRequest: RecountApiRequest,
                           testData: TestData,
                         ): TestIO.Output[TestData, RecountException, Json] = {
    new RecountController(config)
      .recount[EntitiesTestIO](recountRequest)
      .run(testData)
  }

  behaviour of "requesting a recount"

  private val election = SenateElection.`2016`
  private val state = State.ACT
  private val canonicalCountResult = RecountResult(
    election,
    state,
    numVacancies = 2,
    ineligibleCandidates = Set(
      CandidateFixture.ACT.zedSeselja,
    ),
    ProbabilityMeasure.Always(
      CandidateStatuses(asMap = Map.empty),
    )
  )

  it should "return a cached recount if one is present in s3" in {
    val request = RecountApiRequest(election, state, numVacancies = Some(2), ineligibleCandidates = None)

    val cachedContentKey = RecountLocations.locationOfRecountFor(
      RecountRequest(
        request.election,
        request.state,
        request.numVacancies.get,
        canonicalCountResult.ineligibleCandidates.map(_.aecId),
      )
    )

    val testData = TestData(
      AwsTestData(
        s3TestData = S3TestData(
          s3Content = S3TestData.InMemoryS3(
            config.recountDataBucket -> cachedContentKey -> ("{}", ContentType.APPLICATION_JSON),
          ),
        ),
      ),
      EntitiesTestData(
        canonicalCountResults = Map(
          (election, state) -> canonicalCountResult,
        )
      ),
    )

    val output = resultGiven(request, testData)

    assert(output.result === Right(jEmptyObject))
    assert(output.testData.awsTestData.lambdaTestData.invocations === List.empty)
  }

  it should "return a performed recount if one is not present in s3" in {
    val request = RecountApiRequest(election, state, numVacancies = Some(2), ineligibleCandidates = None)

    val testData = TestData(
      AwsTestData(
        lambdaTestData = LambdaTestData(
          handler = {
            case LambdaInvocation(config.recountFunction, payload) => Right("{}")
          },
        ),
        s3TestData = S3TestData(
          s3Content = S3TestData.InMemoryS3.empty.addBucket(config.recountDataBucket),
        ),
      ),
      EntitiesTestData(
        canonicalCountResults = Map(
          (election, state) -> canonicalCountResult,
        )
      ),
    )

    val output = resultGiven(request, testData)

    assert(output.result === Right(jEmptyObject))
    assert(output.testData.awsTestData.lambdaTestData.invocations === List(
      LambdaInvocation(config.recountFunction, Some("""{"election":"2016","state":"ACT","vacancies":2,"ineligibleCandidates":["28773"]}"""))
    ))
  }

}

object RecountControllerSpec {
  final case class TestData(
                             awsTestData: AwsTestData,
                             entitiesTestData: EntitiesTestData = EntitiesTestData.empty,
                           )

  object TestData {
    implicit val testIOInstance: AwsTestData.TestIOInstance[TestData] with EntitiesTestData.TestIOInstance[TestData] =
      new AwsTestData.TestIOInstance[TestData]
        with EntitiesTestData.TestIOInstance[TestData] {
        override protected def awsTestDataField(data: TestData): AwsTestData = data.awsTestData
        override protected def entitiesTestDataField(data: TestData): EntitiesTestData = data.entitiesTestData

        override protected def setAwsTestData(oldData: TestData, newAwsTestData: AwsTestData): TestData =
          oldData.copy(awsTestData = newAwsTestData)
      }
  }

  type EntitiesTestIO[+E, +A] = TestIO[TestData, E, A]
}
