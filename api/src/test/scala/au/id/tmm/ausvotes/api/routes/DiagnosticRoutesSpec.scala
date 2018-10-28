package au.id.tmm.ausvotes.api.routes

import au.id.tmm.ausvotes.api.{MockRequest, MockResponse, Routes}
import au.id.tmm.ausvotes.shared.io.test.BasicTestData
import au.id.tmm.http_constants.HttpMethod
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class DiagnosticRoutesSpec extends ImprovedFlatSpec {

  private val logicForRequest: Routes[BasicTestData.TestIO] = DiagnosticRoutes.apply[BasicTestData.TestIO]

  "the diagnostics route" should "respond with the contents of the version file" in {
    val testLogic = logicForRequest(MockRequest(path = "/diagnostics/version", httpMethod = HttpMethod.GET))

    val testData = BasicTestData(resources = Map("/version.txt" -> "1.0.0"))

    val (_, resultFunction) = testLogic.run(testData)

    assert(resultFunction.map(MockResponse.from).map(_.content) === Right("""{"version":"1.0.0"}"""))
  }

}
