package au.id.tmm.ausvotes.lambdas.recount

import au.id.tmm.ausvotes.lambdas.utils.LambdaHarness
import au.id.tmm.ausvotes.shared.io.exceptions.ExceptionCaseClass
import org.apache.commons.lang3.exception.ExceptionUtils

object RecountLambdaErrorLogTransformer extends LambdaHarness.ErrorLogTransformer[RecountLambdaError] {
  override def messageFor(error: RecountLambdaError): Option[String] = {
    error match {
      case e: RecountLambdaError with ExceptionCaseClass.WithCause => {
        val stacktrace = ExceptionUtils.getStackTrace(e.cause)
        Some(s"$e\n$stacktrace")
      }
      case e => Some(e.toString)
    }
  }
}
