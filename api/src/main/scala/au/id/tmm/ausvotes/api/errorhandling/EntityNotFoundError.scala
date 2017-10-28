package au.id.tmm.ausvotes.api.errorhandling

import au.id.tmm.ausvotes.api.errorhandling.ErrorResponse.ErrorDetails
import au.id.tmm.ausvotes.backend.services.exceptions.{NoSuchDivisionException, NoSuchElectionException, NoSuchEntityException, NoSuchStateException}

final case class EntityNotFoundError(entityType: String, missingEntityId: String) extends ErrorDetails {
  override def render = Map("entityType" -> entityType, "missingEntityId" -> missingEntityId)
}

object EntityNotFoundError {
  def responseFromException(e: NoSuchEntityException): ErrorResponse = {
    val details = e match {
      case e: NoSuchElectionException => EntityNotFoundError("election", e.electionId)
      case e: NoSuchStateException => EntityNotFoundError("state", e.stateAbbreviation)
      case e: NoSuchDivisionException => EntityNotFoundError("division", e.divisionName)
    }

    ErrorResponse(
      errorId = "entity_not_found",
      errorDescription = "the requested entity was not found",
      details = details
    )
  }
}