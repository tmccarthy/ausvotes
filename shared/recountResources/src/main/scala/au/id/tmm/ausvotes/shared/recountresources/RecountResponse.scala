package au.id.tmm.ausvotes.shared.recountresources

import argonaut.Argonaut._
import argonaut.EncodeJson
import au.id.tmm.ausvotes.core.model.codecs.CandidateCodec._
import au.id.tmm.ausvotes.core.model.codecs.PartyCodec.encodeParty
import au.id.tmm.ausvotes.core.model.parsing.Candidate.AecCandidateId

sealed trait RecountResponse

object RecountResponse {

  sealed trait Failure extends RecountResponse

  object Failure {
    final case class RequestDecodeError(message: String, request: String) extends Failure
    final case class InvalidCandidateIds(invalidCandidateAecIds: Set[AecCandidateId]) extends Failure
    case object InternalError extends Failure
  }

  final case class Success(recountResult: RecountResult) extends RecountResponse

  implicit val encodeRecountResponse: EncodeJson[RecountResponse] = {
    case Success(recountResult) =>
      jObjectFields(
        "success" -> true.asJson,
        "recountResult" -> recountResult.asJson,
      )
    case Failure.RequestDecodeError(message, request) =>
      jObjectFields(
        "success" -> false.asJson,
        "errorType" -> "RequestDecodeError".asJson,
        "message" -> message.asJson,
        "request" -> request.asJson,
      )
    case Failure.InvalidCandidateIds(invalidCandidateAecIds) =>
      jObjectFields(
        "success" -> false.asJson,
        "errorType" -> "InvalidCandidateIds".asJson,
        "invalidCandidateIds" -> invalidCandidateAecIds.asJson,
      )
    case Failure.InternalError =>
      jObjectFields(
        "success" -> false.asJson,
        "errorType" -> "InternalError".asJson,
      )
  }

}
