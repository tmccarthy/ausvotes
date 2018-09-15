package au.id.tmm.ausvotes.lambdas.recountenqueue

import argonaut.Argonaut._
import au.id.tmm.ausvotes.lambdas.utils.apigatewayintegration.ApiGatewayLambdaResponse
import au.id.tmm.ausvotes.shared.recountresources.RecountRequest
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class RecountEnqueueLambdaErrorResponseTransformerSpec extends ImprovedFlatSpec {

  private def testErrorResponse(error: RecountEnqueueLambda.Error, expectedMessage: String = "An error occurred", expectedCode: Int = 500): Unit = {
    it should s"translate $error appropriately" in {
      val expectedResponse = ApiGatewayLambdaResponse(
        statusCode = expectedCode,
        headers = Map.empty,
        body = jObjectFields(
          "message" -> expectedMessage.asJson,
        )
      )

      assert(RecountEnqueueLambdaErrorResponseTransformer.responseFor(error) === expectedResponse)
    }
  }

  behaviour of "the recount enqueue lambda error response transformer"

  testErrorResponse(
    error = RecountEnqueueLambda.Error.MessagePublishError(new Exception),
  )

  testErrorResponse(
    error = RecountEnqueueLambda.Error.BadRequestError(RecountRequest.Error.MissingElection),
    expectedMessage = "Election was not specified",
    expectedCode = 400,
  )

  testErrorResponse(
    error = RecountEnqueueLambda.Error.CheckRecountComputedError(new Exception),
  )

  testErrorResponse(
    error = RecountEnqueueLambda.Error.RecountQueueArnMissing,
  )

  testErrorResponse(
    error = RecountEnqueueLambda.Error.RecountDataBucketMissing,
  )

  testErrorResponse(
    error = RecountEnqueueLambda.Error.RegionMissing,
  )

}
