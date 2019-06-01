package au.id.tmm.ausvotes.api.routes

import au.id.tmm.ausvotes.api.errors.NotFoundException
import au.id.tmm.ausvotes.api.{CompleteRoutes, MockRequest}
import au.id.tmm.ausvotes.shared.io.test.BasicTestData
import au.id.tmm.ausvotes.shared.io.test.BasicTestData.BasicTestIO
import au.id.tmm.http_constants.HttpMethod
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class NotFoundRouteSpec extends ImprovedFlatSpec {

  private val logicForRequest: CompleteRoutes[BasicTestIO] = NotFoundRoute.apply[BasicTestIO]

  "the diagnostics route" should "respond with the contents of the version file" in {
    val testLogic = logicForRequest(MockRequest(path = "/resource", httpMethod = HttpMethod.GET))

    val resultFunction = testLogic.runEither(BasicTestData())

    assert(resultFunction === Left(NotFoundException("/resource")))
  }

}
