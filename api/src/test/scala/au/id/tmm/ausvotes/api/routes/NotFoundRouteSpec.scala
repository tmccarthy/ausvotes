package au.id.tmm.ausvotes.api.routes

import au.id.tmm.ausvotes.api.errors.NotFoundException
import au.id.tmm.ausvotes.api.{MockRequest, Routes}
import au.id.tmm.ausvotes.shared.io.test.BasicTestData
import au.id.tmm.http_constants.HttpMethod
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class NotFoundRouteSpec extends ImprovedFlatSpec {

  private val logicForRequest: Routes[BasicTestData.TestIO] = NotFoundRoute.apply[BasicTestData.TestIO]

  "the diagnostics route" should "respond with the contents of the version file" in {
    val testLogic = logicForRequest(MockRequest(path = "/resource", httpMethod = HttpMethod.GET))

    val (_, resultFunction) = testLogic.run(BasicTestData())

    assert(resultFunction === Left(NotFoundException()))
  }

}
