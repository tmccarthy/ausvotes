package au.id.tmm.ausvotes.api.errorhandling

import au.id.tmm.ausvotes.api.errorhandling.ErrorResponse.ErrorDetails

final case class ErrorResponse(errorId: String, errorDescription: String, details: ErrorDetails)

object ErrorResponse {
  trait ErrorDetails {
    def render: Map[String, String]
  }

  object ErrorDetails {
    case object Empty extends ErrorDetails {
      val render = Map()
    }
  }
}
