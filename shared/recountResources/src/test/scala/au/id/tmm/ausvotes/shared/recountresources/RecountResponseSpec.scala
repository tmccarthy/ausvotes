package au.id.tmm.ausvotes.shared.recountresources

import argonaut.Argonaut._
import au.id.tmm.ausvotes.core.model.parsing.Candidate.AecCandidateId
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class RecountResponseSpec extends ImprovedFlatSpec {

  "the recount response encoder" can "encode a failure due to a bad request" in {
    val failure: RecountResponse = RecountResponse.Failure.RequestDecodeError("Invalid json", "{")

    val expectedJson = jObjectFields(
      "success" -> false.asJson,
      "errorType" -> "RequestDecodeError".asJson,
      "message" -> "Invalid json".asJson,
      "request" -> "{".asJson,
    )

    assert(failure.asJson === expectedJson)
  }

  it can "encode a failure due to invalid candidate ids" in {
    val failure: RecountResponse = RecountResponse.Failure.InvalidCandidateIds(Set(AecCandidateId("123"), AecCandidateId("456")))

    val expectedJson = jObjectFields(
      "success" -> false.asJson,
      "errorType" -> "InvalidCandidateIds".asJson,
      "invalidCandidateIds" -> jArrayElements(
        "123".asJson,
        "456".asJson,
      ),
    )

    assert(failure.asJson === expectedJson)
  }

  it can "encode an internal error" in {
    val failure: RecountResponse = RecountResponse.Failure.InternalError

    val expectedJson = jObjectFields(
      "success" -> false.asJson,
      "errorType" -> "InternalError".asJson,
    )

    assert(failure.asJson === expectedJson)
  }

  it can "encode a success" in {
    val recountResponse: RecountResponse = RecountResponse.Success(
      CountResultSpec.recountResultFixture
    )

    val expectedJson = jObjectFields(
      "success" -> true.asJson,
      "recountResult" -> CountResultSpec.recountResultFixture.asJson,
    )

    assert(recountResponse.asJson === expectedJson)
  }

}
