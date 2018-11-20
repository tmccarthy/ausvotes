package au.id.tmm.ausvotes.shared.aws.testing

import au.id.tmm.ausvotes.shared.aws.testing.testdata.{LambdaTestData, S3TestData, SnsTestData}
import au.id.tmm.ausvotes.shared.io.test
import au.id.tmm.ausvotes.shared.io.test.BasicTestData

final case class AwsTestData(
                              basicTestData: BasicTestData = BasicTestData(),

                              s3TestData: S3TestData = S3TestData.empty,
                              snsTestData: SnsTestData = SnsTestData.empty,
                              lambdaTestData: LambdaTestData = LambdaTestData.default,
                            )

object AwsTestData {

  type AwsTestIO[+E, +A] = test.TestIO[AwsTestData, E, A]

  trait TestIOInstance[D]
    extends BasicTestData.TestIOInstance[D]
      with S3TestData.TestIOInstance[D]
      with SnsTestData.TestIOInstance[D]
      with LambdaTestData.TestIOInstance[D] {

    protected def awsTestDataField(data: D): AwsTestData
    protected def setAwsTestData(oldData: D, newAwsTestData: AwsTestData): D

    override protected def basicTestDataField(data: D): BasicTestData = awsTestDataField(data).basicTestData
    override protected def s3TestDataField(data: D): S3TestData = awsTestDataField(data).s3TestData
    override protected def snsTestDataField(data: D): SnsTestData = awsTestDataField(data).snsTestData
    override protected def lambdaTestDataField(data: D): LambdaTestData = awsTestDataField(data).lambdaTestData

    override protected def setBasicTestDataField(oldData: D, newBasicTestData: BasicTestData): D =
      setAwsTestData(oldData, awsTestDataField(oldData).copy(basicTestData = newBasicTestData))

    override protected def setS3TestData(oldData: D, newS3TestData: S3TestData): D =
      setAwsTestData(oldData, awsTestDataField(oldData).copy(s3TestData = newS3TestData))

    override protected def setSnsTestData(oldData: D, newSnsTestData: SnsTestData): D =
      setAwsTestData(oldData, awsTestDataField(oldData).copy(snsTestData = newSnsTestData))

    override protected def setLambdaTestData(oldData: D, newLambdaTestData: LambdaTestData): D =
      setAwsTestData(oldData, awsTestDataField(oldData).copy(lambdaTestData = newLambdaTestData))
  }

  implicit val testIOInstance: TestIOInstance[AwsTestData] = new TestIOInstance[AwsTestData] {
    override protected def awsTestDataField(data: AwsTestData): AwsTestData = data

    override protected def setAwsTestData(oldData: AwsTestData, newAwsTestData: AwsTestData): AwsTestData = newAwsTestData
  }

}
