package au.id.tmm.ausvotes.shared.io.test

import au.id.tmm.ausvotes.shared.io.test.testdata.{EnvVarTestData, LoggingTestData, TimerTestData}
import au.id.tmm.bfect.effects.extra.Resources
import au.id.tmm.bfect.testing.BState

final case class BasicTestData(
                                currentTimeTestData: TimerTestData = TimerTestData.default,
                                envVarTestData: EnvVarTestData = EnvVarTestData.empty,
                                loggingTestData: LoggingTestData = LoggingTestData.empty,
                              )

object BasicTestData {

  type BasicTestIO[+E, +A] = BState[BasicTestData, E, A]

  trait TestIOInstance[D]
    extends BState.TimerInstance[D]
      with TimerTestData.TestIOInstance[D]
      with LoggingTestData.TestIOInstance[D]
      with EnvVarTestData.TestIOInstance[D]
      with Resources.Live[BState[D, +?, +?]] {

    protected def basicTestDataField(data: D): BasicTestData
    protected def setBasicTestDataField(oldData: D, newBasicTestData: BasicTestData): D

    override protected def currentTimeField(data: D): TimerTestData = basicTestDataField(data).currentTimeTestData
    override protected def loggingTestDataField(data: D): LoggingTestData = basicTestDataField(data).loggingTestData
    override protected def envVarsField(data: D): EnvVarTestData = basicTestDataField(data).envVarTestData

    override protected def setCurrentTimeField(oldData: D, newCurrentTestTimeData: TimerTestData): D =
      setBasicTestDataField(oldData, basicTestDataField(oldData).copy(currentTimeTestData = newCurrentTestTimeData))

    override protected def setLoggingTestData(oldData: D, newLoggingTestData: LoggingTestData): D =
      setBasicTestDataField(oldData, basicTestDataField(oldData).copy(loggingTestData = newLoggingTestData))
  }

  implicit val testIOInstance: TestIOInstance[BasicTestData] = new TestIOInstance[BasicTestData] {
    override protected def basicTestDataField(data: BasicTestData): BasicTestData = data

    override protected def setBasicTestDataField(oldData: BasicTestData, newBasicTestData: BasicTestData): BasicTestData = newBasicTestData
  }

}
