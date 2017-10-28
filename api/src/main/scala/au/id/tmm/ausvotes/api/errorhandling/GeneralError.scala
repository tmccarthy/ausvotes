package au.id.tmm.ausvotes.api.errorhandling

import au.id.tmm.ausvotes.api.errorhandling.ErrorResponse.ErrorDetails
import com.google.common.base.MoreObjects
import org.apache.commons.lang3.exception.ExceptionUtils

final case class GeneralError(
                               exception: Throwable,
                             ) extends ErrorDetails {
  override def render = Map(
    "exceptionClass" -> exception.getClass.getName,
    "message" -> MoreObjects.firstNonNull(exception.getMessage, ""),
    "stacktrace" -> ExceptionUtils.getStackTrace(exception),
  )
}

object GeneralError {
  def responseFromException(isDevelopmentMode: Boolean, e: Throwable): ErrorResponse = {
    val details = if (isDevelopmentMode) GeneralError(e) else ErrorDetails.Empty

    ErrorResponse(
      errorId = "generalError",
      errorDescription = "an error occurred",
      details = details,
    )
  }
}
