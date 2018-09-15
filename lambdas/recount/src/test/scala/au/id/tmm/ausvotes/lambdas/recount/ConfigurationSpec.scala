package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.shared.aws.data.S3BucketName
import au.id.tmm.ausvotes.shared.io.test.BasicTestData
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ConfigurationSpec extends ImprovedFlatSpec {

  private val logicUnderTest = Configuration.recountDataBucketName[BasicTestData.TestIO]

  "the recount lambda configurartion" should "read the 'RECOUNT_DATA_BUCKET' env var" in {
    val testData = BasicTestData(envVars = Map("RECOUNT_DATA_BUCKET" -> "test"))

    val (_, errorOrBucketName) = logicUnderTest.run(testData)

    assert(errorOrBucketName === Right(S3BucketName("test")))
  }

  it should "fail if the recount data bucket is missing from the env vars" in {
    val testData = BasicTestData(envVars = Map.empty)

    val (_, errorOrBucketName) = logicUnderTest.run(testData)

    assert(errorOrBucketName === Left(RecountLambdaError.RecountDataBucketUndefined))
  }

}
