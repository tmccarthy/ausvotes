package au.id.tmm.ausvotes.api.routes

import java.io.{IOException, InputStream}
import java.nio.charset.Charset

import au.id.tmm.ausvotes.api.{MockRequest, MockResponse, PartialRoutes}
import au.id.tmm.bfect.effects.extra.Resources
import au.id.tmm.bfect.testing.BState
import au.id.tmm.http_constants.HttpMethod
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class DiagnosticRoutesSpec extends ImprovedFlatSpec {

  private type TestIO[+E, +A] = BState[Map[String, String], E, A]

  //noinspection NotImplementedCode
  private implicit val resources: Resources[TestIO] = new Resources[TestIO] {
    override def getResourceAsStream(resourceName: String): TestIO[Nothing, Option[InputStream]] = ???
    override def useResourceAsStream[E, A](resourceName: String)(use: InputStream => TestIO[E, A]): TestIO[Resources.ResourceStreamError[E], A] = ???
    override def resourceAsString(resourceName: String, charset: Charset): TestIO[Resources.ResourceStreamError[IOException], String] =
      BState.inspect(_.get(resourceName).toRight(Resources.ResourceStreamError.ResourceNotFound))
  }

  private val logicForRequest: PartialRoutes[TestIO] = DiagnosticRoutes.apply[TestIO]

  "the diagnostics route" should "respond with the contents of the version file" in {
    val testLogic = logicForRequest(MockRequest(path = "/diagnostics/version", httpMethod = HttpMethod.GET))

    val resultFunction = testLogic.runEither(Map("/version.txt" -> "1.0.0"))

    assert(resultFunction.map(MockResponse.from).map(_.content) === Right("""{"version":"1.0.0"}"""))
  }

  it should "respond with an error if the version file is missing" in {
    val testLogic = logicForRequest(MockRequest(path = "/diagnostics/version", httpMethod = HttpMethod.GET))

    val resultFunction = testLogic.runEither(Map.empty)

    assert(resultFunction.map(MockResponse.from).left.map(_.getMessage) === Left("Version file missing"))
  }

}
