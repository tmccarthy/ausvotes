package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.shared.aws.data.S3BucketName
import au.id.tmm.ausvotes.shared.io.test.BasicTestData
import au.id.tmm.ausvotes.shared.io.test.BasicTestData.BasicTestIO
import au.id.tmm.ausvotes.shared.io.test.testdata.EnvVarTestData
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class ConfigurationSpec extends ImprovedFlatSpec {

  private val logicUnderTest = Configuration.recountDataBucketName[BasicTestIO]

  "the recount lambda configuration" should "read the 'RECOUNT_DATA_BUCKET' env var" in {
    val testData = BasicTestData(envVarTestData = EnvVarTestData(envVars = Map("RECOUNT_DATA_BUCKET" -> "test")))

    val errorOrBucketName = logicUnderTest.runEither(testData)

    assert(errorOrBucketName === Right(S3BucketName("test")))
  }

  it should "fail if the recount data bucket is missing from the env vars" in {
    val testData = BasicTestData()

    val errorOrBucketName = logicUnderTest.runEither(testData)

    assert(errorOrBucketName === Left(RecountLambdaError.RecountDataBucketUndefined()))
  }

}
