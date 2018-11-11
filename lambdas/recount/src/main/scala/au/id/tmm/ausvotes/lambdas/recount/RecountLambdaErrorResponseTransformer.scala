package au.id.tmm.ausvotes.lambdas.recount

import argonaut.Argonaut.{jObjectFields, jString}
import au.id.tmm.ausvotes.lambdas.utils.LambdaHarness
import au.id.tmm.ausvotes.lambdas.utils.apigatewayintegration.ApiGatewayLambdaResponse

object RecountLambdaErrorResponseTransformer
  extends LambdaHarness.ErrorResponseTransformer[ApiGatewayLambdaResponse, RecountLambdaError] {

  private[recount] def badRequestResponse(message: String): ApiGatewayLambdaResponse = ApiGatewayLambdaResponse(
    statusCode = 400,
    headers = Map.empty,
    body = jObjectFields(
      "message" -> jString(message),
    )
  )

  override def responseFor(error: RecountLambdaError): ApiGatewayLambdaResponse = error match {
    case RecountLambdaError.RecountRequestError.InvalidCandidateIds(invalidCandidateAecIds) =>
      badRequestResponse(s"""Invalid candidate ids ${invalidCandidateAecIds.mkString("[\"", "\", \"", "\"]")}""")

    case RecountLambdaError.RecountDataBucketUndefined =>
      badRequestResponse("Recount data bucket was undefined")

    case RecountLambdaError.EntityFetchError(_) | RecountLambdaError.EntityCachePopulationError(_) =>
      badRequestResponse("An error occurred while fetching the entities")

    case RecountLambdaError.RecountComputationError(_) =>
      badRequestResponse("An error occurred while performing the recount computation")

    case RecountLambdaError.WriteRecountError(_) =>
      badRequestResponse("An error occurred while writing the recount result")
  }
}
