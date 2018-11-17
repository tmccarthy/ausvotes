package au.id.tmm.ausvotes.api.routes

import au.id.tmm.ausvotes.api.MockResponse
import au.id.tmm.ausvotes.api.errors.NotFoundException
import au.id.tmm.ausvotes.shared.io.actions.Log
import au.id.tmm.ausvotes.shared.io.actions.Log.LoggedEvent
import au.id.tmm.ausvotes.shared.io.test.BasicTestData.BasicTestIO
import au.id.tmm.ausvotes.shared.io.test.{BasicTestData, TestIO}
import au.id.tmm.http_constants.HttpResponseCode
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class AppRoutesSpec extends ImprovedFlatSpec {

  private def responseAndLoggedMessagesWhenHandling(exception: Exception): (Map[Log.Level, List[LoggedEvent]], MockResponse) = {
    val logicUnderTest = AppRoutes.handleException[BasicTestIO](exception)

    val TestIO.Output(testData, responseFunction) = logicUnderTest.run(BasicTestData())

    //noinspection NotImplementedCode
    val response = MockResponse.from(responseFunction.getOrElse(???))

    (testData.loggingTestData.loggedMessages, response)
  }

  "exception handling" should "handle a NotFoundException" in {
    val (loggedMessages, response) = responseAndLoggedMessagesWhenHandling(NotFoundException("/missing"))

    assert(response.responseCode === HttpResponseCode.NotFound)
    assert(response.content === """{"message":"Not found \"/missing\""}""")
    assert(loggedMessages(Log.Level.Info) === List(LoggedEvent("NOT_FOUND_RESPONSE", List("path" -> "/missing"), None)))
  }

  it should "handle a generic exception" in {
    val exception = new Exception

    val (loggedMessages, response) = responseAndLoggedMessagesWhenHandling(exception)

    assert(response.responseCode === HttpResponseCode.InternalServerError)
    assert(response.content === """{"message":"An error occurred"}""")
    assert(loggedMessages(Log.Level.Error) === List(LoggedEvent("ERROR_RESPONSE", List.empty, Some(exception))))
  }

}
