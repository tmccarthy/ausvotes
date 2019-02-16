package au.id.tmm.ausvotes.shared.io.test

import au.id.tmm.ausvotes.shared.io.test.testdata.{CurrentTimeTestData, EnvVarTestData, LoggingTestData, ResourcesTestData}

final case class BasicTestData(
                                currentTimeTestData: CurrentTimeTestData = CurrentTimeTestData.default,
                                envVarTestData: EnvVarTestData = EnvVarTestData.empty,
                                loggingTestData: LoggingTestData = LoggingTestData.empty,
                                resourcesTestData: ResourcesTestData = ResourcesTestData.empty,
                              )

object BasicTestData {

  type BasicTestIO[+E, +A] = TestIO[BasicTestData, E, A]

  implicit class Ops[+E, +A](basicTestIO: BasicTestIO[E, A]) {
    def runUnsafe: A = basicTestIO.run(BasicTestData()).result match {
      case Right(value) => value
      case Left(e: Throwable) => throw e
      case Left(e) => throw new RuntimeException(e.toString)
    }
  }

  trait TestIOInstance[D]
    extends CurrentTimeTestData.TestIOInstance[D]
      with LoggingTestData.TestIOInstance[D]
      with EnvVarTestData.TestIOInstance[D]
      with ResourcesTestData.TestIOInstance[D] {

    protected def basicTestDataField(data: D): BasicTestData
    protected def setBasicTestDataField(oldData: D, newBasicTestData: BasicTestData): D

    override protected def currentTimeField(data: D): CurrentTimeTestData = basicTestDataField(data).currentTimeTestData
    override protected def loggingTestDataField(data: D): LoggingTestData = basicTestDataField(data).loggingTestData
    override protected def envVarsField(data: D): EnvVarTestData = basicTestDataField(data).envVarTestData
    override protected def resourcesField(data: D): ResourcesTestData = basicTestDataField(data).resourcesTestData

    override protected def setCurrentTimeField(oldData: D, newCurrentTestTimeData: CurrentTimeTestData): D =
      setBasicTestDataField(oldData, basicTestDataField(oldData).copy(currentTimeTestData = newCurrentTestTimeData))

    override protected def setLoggingTestData(oldData: D, newLoggingTestData: LoggingTestData): D =
      setBasicTestDataField(oldData, basicTestDataField(oldData).copy(loggingTestData = newLoggingTestData))
  }

  implicit val testIOInstance: TestIOInstance[BasicTestData] = new TestIOInstance[BasicTestData] {
    override protected def basicTestDataField(data: BasicTestData): BasicTestData = data

    override protected def setBasicTestDataField(oldData: BasicTestData, newBasicTestData: BasicTestData): BasicTestData = newBasicTestData
  }

}
