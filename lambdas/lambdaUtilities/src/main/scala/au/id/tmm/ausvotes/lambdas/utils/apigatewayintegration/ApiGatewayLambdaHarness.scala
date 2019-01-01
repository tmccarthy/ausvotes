package au.id.tmm.ausvotes.lambdas.utils.apigatewayintegration

import au.id.tmm.ausvotes.lambdas.utils.LambdaHarness
import au.id.tmm.ausvotes.lambdas.utils.LambdaHarness._
import io.circe.Json

abstract class ApiGatewayLambdaHarness[T_ERROR]
  extends LambdaHarness[ApiGatewayLambdaRequest, ApiGatewayLambdaResponse, T_ERROR] {

  protected def transformHarnessError(harnessInputError: HarnessInputError): ApiGatewayLambdaResponse =
    harnessInputError match {
      case RequestReadError(_) => ApiGatewayLambdaResponse(500, Map.empty, Json.fromString(""))
      case RequestDecodeError(message, _) => ApiGatewayLambdaResponse(400, Map.empty, Json.fromString(message))
    }

}
