package au.id.tmm.ausvotes.shared.recountresources

import au.id.tmm.ausvotes.model.Candidate
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.Json
import io.circe.syntax.EncoderOps

class RecountResponseSpec extends ImprovedFlatSpec {

  "the recount response encoder" can "encode a failure due to a bad request" in {
    val failure: RecountResponse = RecountResponse.Failure.RequestDecodeError("Invalid json", "{")

    val expectedJson = Json.obj(
      "success" -> false.asJson,
      "errorType" -> "RequestDecodeError".asJson,
      "message" -> "Invalid json".asJson,
      "request" -> "{".asJson,
    )

    assert(failure.asJson === expectedJson)
  }

  it can "encode a failure due to invalid candidate ids" in {
    val failure: RecountResponse = RecountResponse.Failure.InvalidCandidateIds(Set(Candidate.Id(123), Candidate.Id(456)))

    val expectedJson = Json.obj(
      "success" -> false.asJson,
      "errorType" -> "InvalidCandidateIds".asJson,
      "invalidCandidateIds" -> Json.arr(
        123.asJson,
        456.asJson,
      ),
    )

    assert(failure.asJson === expectedJson)
  }

  it can "encode an internal error" in {
    val failure: RecountResponse = RecountResponse.Failure.InternalError

    val expectedJson = Json.obj(
      "success" -> false.asJson,
      "errorType" -> "InternalError".asJson,
    )

    assert(failure.asJson === expectedJson)
  }

  it can "encode a success" in {
    val recountResponse: RecountResponse = RecountResponse.Success(
      CountSummarySpec.recountResultFixture
    )

    val expectedJson = Json.obj(
      "success" -> true.asJson,
      "recountResult" -> CountSummarySpec.recountResultFixture.asJson,
    )

    assert(recountResponse.asJson === expectedJson)
  }

}
