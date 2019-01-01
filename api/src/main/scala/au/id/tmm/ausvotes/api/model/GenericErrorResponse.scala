package au.id.tmm.ausvotes.api.model

import io.circe.Encoder

final case class GenericErrorResponse(message: String = "An error occurred")

object GenericErrorResponse {
  implicit val encodeGenericErrorResponse: Encoder[GenericErrorResponse] = Encoder.forProduct1("message")(_.message)
}
