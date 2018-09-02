package au.id.tmm.ausvotes.lambdas.recountenqueue

import argonaut.Argonaut._
import au.id.tmm.ausvotes.lambdas.utils.LambdaHarness
import au.id.tmm.ausvotes.lambdas.utils.apigatewayintegration.ApiGatewayLambdaResponse
import au.id.tmm.ausvotes.shared.recountresources.RecountRequest

object RecountEnqueueLambdaErrorResponseTransformer extends LambdaHarness.ErrorResponseTransformer[ApiGatewayLambdaResponse, RecountEnqueueLambda.Error] {

  override def responseFor(error: RecountEnqueueLambda.Error): ApiGatewayLambdaResponse = error match {
    case RecountEnqueueLambda.Error.BadRequestError(cause) =>
      errorResponse(isClientError = true, message = Some(RecountRequest.Error.humanReadableMessageFor(cause)))

    case RecountEnqueueLambda.Error.MessagePublishError(_) |
         RecountEnqueueLambda.Error.CheckRecountComputedError(_) |
         RecountEnqueueLambda.Error.RecountQueueArnMissing |
         RecountEnqueueLambda.Error.RecountDataBucketMissing |
         RecountEnqueueLambda.Error.RegionMissing =>
      errorResponse(isClientError = false, message = None)
  }

  private def errorResponse(isClientError: Boolean, message: Option[String]): ApiGatewayLambdaResponse =
    ApiGatewayLambdaResponse(
      statusCode = if (isClientError) 400 else 500,
      headers = Map.empty,
      body = jObjectFields(
        "message" -> message.getOrElse("An error occurred").asJson,
      )
    )
}
