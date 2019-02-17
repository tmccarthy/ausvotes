package au.id.tmm.ausvotes.shared.recountresources

import au.id.tmm.ausvotes.model.CandidateDetails
import io.circe.syntax.EncoderOps
import io.circe.{Encoder, Json}

sealed trait RecountResponse

object RecountResponse {

  sealed trait Failure extends RecountResponse

  object Failure {
    final case class RequestDecodeError(message: String, request: String) extends Failure
    final case class InvalidCandidateIds(invalidCandidateAecIds: Set[CandidateDetails.Id]) extends Failure
    case object InternalError extends Failure
  }

  final case class Success(recountResult: CountSummary) extends RecountResponse

  implicit val encodeRecountResponse: Encoder[RecountResponse] = {
    case Success(recountResult) =>
      Json.obj(
        "success" -> true.asJson,
        "recountResult" -> recountResult.asJson,
      )
    case Failure.RequestDecodeError(message, request) =>
      Json.obj(
        "success" -> false.asJson,
        "errorType" -> "RequestDecodeError".asJson,
        "message" -> message.asJson,
        "request" -> request.asJson,
      )
    case Failure.InvalidCandidateIds(invalidCandidateAecIds) =>
      Json.obj(
        "success" -> false.asJson,
        "errorType" -> "InvalidCandidateIds".asJson,
        "invalidCandidateIds" -> invalidCandidateAecIds.asJson,
      )
    case Failure.InternalError =>
      Json.obj(
        "success" -> false.asJson,
        "errorType" -> "InternalError".asJson,
      )
  }

}
