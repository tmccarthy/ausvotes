package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.lambdas.utils.ApiGatewayLambdaHarness
import org.apache.commons.lang3.exception.ExceptionUtils

object RecountLambdaErrorLogTransformer extends ApiGatewayLambdaHarness.ErrorLogTransformer[RecountLambdaError] {
  override def messageFor(error: RecountLambdaError): Option[String] = {
    error match {
      case e: RecountLambdaError.WithException => {
        val stacktrace = ExceptionUtils.getStackTrace(e.exception)
        Some(s"$e\n$stacktrace")
      }
      case e => Some(e.toString)
    }
  }
}
