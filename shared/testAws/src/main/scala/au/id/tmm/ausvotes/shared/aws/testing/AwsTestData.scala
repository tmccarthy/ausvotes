package au.id.tmm.ausvotes.shared.aws.testing

import au.id.tmm.ausvotes.shared.aws.actions.{LambdaActions, S3Actions, SnsActions}
import au.id.tmm.ausvotes.shared.aws.testing.testdata.{LambdaTestData, S3TestData, SnsTestData}
import au.id.tmm.ausvotes.shared.io.actions.{EnvVars, Log, Now, Resources}
import au.id.tmm.ausvotes.shared.io.test
import au.id.tmm.ausvotes.shared.io.test.BasicTestData
import au.id.tmm.ausvotes.shared.io.test.testdata.{CurrentTimeTestData, EnvVarTestData, LoggingTestData, ResourcesTestData}

final case class AwsTestData(
                              basicTestData: BasicTestData = BasicTestData(),

                              s3TestData: S3TestData = S3TestData.empty,
                              snsTestData: SnsTestData = SnsTestData.empty,
                              lambdaTestData: LambdaTestData = LambdaTestData.default,
                            )

object AwsTestData {

  type AwsTestIO[+E, +A] = test.TestIO[AwsTestData, E, A]

  implicit val writesS3Instance: S3Actions.WritesToS3[AwsTestIO] = S3TestData.testIoWriteS3Instance[AwsTestData](_.s3TestData, (data, s3Data) => data.copy(s3TestData = s3Data))
  implicit val readsS3Instance: S3Actions.ReadsS3[AwsTestIO] = S3TestData.testIoReadS3Instance[AwsTestData](_.s3TestData)
  implicit val lambdaTestingInstance: LambdaActions.InvokesLambda[AwsTestIO] = LambdaTestData.testIOInstance[AwsTestData](_.lambdaTestData, (data, lambdaData) => data.copy(lambdaTestData = lambdaData))
  implicit val snsTestData: SnsActions.PutsSnsMessages[AwsTestIO] = SnsTestData.testIOInstance[AwsTestData](_.snsTestData, (data, snsData) => data.copy(snsTestData = snsData))

  implicit val nowInstance: Now[AwsTestIO] = CurrentTimeTestData.testIOInstance[AwsTestData](
    currentTimeField = _.basicTestData.currentTimeTestData,
    setCurrentTimeField = (data, newCurrentTimeTestData) => data.copy(basicTestData = data.basicTestData.copy(currentTimeTestData = newCurrentTimeTestData)),
  )

  implicit val loggingInstance: Log[AwsTestIO] = LoggingTestData.testIOInstance[AwsTestData](
    loggingTestDataField = _.basicTestData.loggingTestData,
    setLoggingTestData = (data, newLoggingData) => data.copy(basicTestData = data.basicTestData.copy(loggingTestData = newLoggingData)),
  )

  implicit val envVarInstance: EnvVars[AwsTestIO] = EnvVarTestData.testIOInstance[AwsTestData](_.basicTestData.envVarTestData)
  implicit val resourcesInstance: Resources[AwsTestIO] = ResourcesTestData.testIOInstance[AwsTestData](_.basicTestData.resourcesTestData)

}
