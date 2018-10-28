package au.id.tmm.ausvotes.api.model.diagnostics

import argonaut.Argonaut._
import argonaut.EncodeJson

final case class VersionResponse(version: String)

object VersionResponse {
  implicit val encodeVersionResponse: EncodeJson[VersionResponse] =
    jencode1L[VersionResponse, String](_.version)("version")
}
