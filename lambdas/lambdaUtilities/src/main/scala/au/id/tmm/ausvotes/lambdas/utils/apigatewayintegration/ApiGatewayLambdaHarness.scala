package au.id.tmm.ausvotes.lambdas.utils.apigatewayintegration

import argonaut.Argonaut._
import au.id.tmm.ausvotes.lambdas.utils.LambdaHarness
import au.id.tmm.ausvotes.lambdas.utils.LambdaHarness._

abstract class ApiGatewayLambdaHarness[T_ERROR]
  extends LambdaHarness[ApiGatewayLambdaRequest, ApiGatewayLambdaResponse, T_ERROR] {

  protected def transformHarnessError(harnessInputError: HarnessInputError): ApiGatewayLambdaResponse =
    harnessInputError match {
      case RequestReadError(_) => ApiGatewayLambdaResponse(500, Map.empty, jString(""))
      case RequestDecodeError(message) => ApiGatewayLambdaResponse(400, Map.empty, jString(message))
    }

}
