package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.lambdas.utils.LambdaHarness
import au.id.tmm.ausvotes.lambdas.utils.apigatewayintegration.ApiGatewayLambdaResponse
import au.id.tmm.ausvotes.shared.recountresources.recount.RunRecount
import io.circe.Json

object RecountLambdaErrorResponseTransformer
  extends LambdaHarness.ErrorResponseTransformer[ApiGatewayLambdaResponse, RecountLambdaError] {

  private[recount] def badRequestResponse(message: String): ApiGatewayLambdaResponse = ApiGatewayLambdaResponse(
    statusCode = 400,
    headers = Map.empty,
    body = Json.obj(
      "message" -> Json.fromString(message),
    )
  )

  override def responseFor(error: RecountLambdaError): ApiGatewayLambdaResponse = error match {
    case RecountLambdaError.RecountComputationError(RunRecount.Error.InvalidCandidateIds(invalidCandidateAecIds)) =>
      badRequestResponse(s"""Invalid candidate ids ${invalidCandidateAecIds.map(_.asInt).mkString("[", ", ", "]")}""")

    case RecountLambdaError.RecountComputationError(_) =>
    badRequestResponse("An error occurred while performing the recount computation")

    case RecountLambdaError.RecountDataBucketUndefined() =>
      badRequestResponse("Recount data bucket was undefined")

    case RecountLambdaError.WriteRecountError(_) =>
      badRequestResponse("An error occurred while writing the recount result")
  }
}
