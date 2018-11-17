package au.id.tmm.ausvotes.shared.io.test

import au.id.tmm.ausvotes.shared.io.actions.{EnvVars, Log, Now, Resources}
import au.id.tmm.ausvotes.shared.io.test.testdata.{CurrentTimeTestData, EnvVarTestData, LoggingTestData, ResourcesTestData}

final case class BasicTestData(
                                currentTimeTestData: CurrentTimeTestData = CurrentTimeTestData.default,
                                envVarTestData: EnvVarTestData = EnvVarTestData.empty,
                                loggingTestData: LoggingTestData = LoggingTestData.empty,
                                resourcesTestData: ResourcesTestData = ResourcesTestData.empty,
                              )

object BasicTestData {

  type BasicTestIO[+E, +A] = TestIO[BasicTestData, E, A]

  implicit val nowInstance: Now[BasicTestIO] = CurrentTimeTestData.testIOInstance[BasicTestData](
    currentTimeField = _.currentTimeTestData,
    setCurrentTimeField = (data, newCurrentTimeTestData) => data.copy(currentTimeTestData = newCurrentTimeTestData),
  )

  implicit val loggingInstance: Log[BasicTestIO] = LoggingTestData.testIOInstance[BasicTestData](
    loggingTestDataField = _.loggingTestData,
    setLoggingTestData = (data, newLoggingData) => data.copy(loggingTestData = newLoggingData),
  )

  implicit val envVarInstance: EnvVars[BasicTestIO] = EnvVarTestData.testIOInstance[BasicTestData](_.envVarTestData)
  implicit val resourcesInstance: Resources[BasicTestIO] = ResourcesTestData.testIOInstance[BasicTestData](_.resourcesTestData)

}
