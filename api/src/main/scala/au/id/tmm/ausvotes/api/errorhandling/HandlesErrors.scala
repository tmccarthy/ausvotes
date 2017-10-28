package au.id.tmm.ausvotes.api.errorhandling

import au.id.tmm.ausvotes.backend.services.exceptions.NoSuchEntityException
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.{InternalServerError, NotFound, ScalatraServlet}

trait HandlesErrors { this: ScalatraServlet with JacksonJsonSupport =>

  // TODO logging

  error {
    case e: NoSuchEntityException => NotFound(EntityNotFoundError.responseFromException(e))
    case e => InternalServerError(GeneralError.responseFromException(isDevelopmentMode, e))
  }

}
