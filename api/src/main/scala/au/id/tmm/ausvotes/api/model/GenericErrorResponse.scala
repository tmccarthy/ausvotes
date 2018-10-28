package au.id.tmm.ausvotes.api.model

import argonaut.Argonaut._
import argonaut.EncodeJson

final case class GenericErrorResponse(message: String = "An error occurred")

object GenericErrorResponse {
  implicit val encodeGenericErrorResponse: EncodeJson[GenericErrorResponse] =
    jencode1L[GenericErrorResponse, String](_.message)("message")
}
