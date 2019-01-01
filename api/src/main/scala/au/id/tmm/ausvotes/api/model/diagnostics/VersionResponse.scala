package au.id.tmm.ausvotes.api.model.diagnostics

import io.circe.Encoder

final case class VersionResponse(version: String)

object VersionResponse {
  implicit val encodeVersionResponse: Encoder[VersionResponse] = Encoder.forProduct1("version")(_.version)
}
