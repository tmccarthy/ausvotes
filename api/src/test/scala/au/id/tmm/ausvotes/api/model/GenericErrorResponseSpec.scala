package au.id.tmm.ausvotes.api.model

import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.Json
import io.circe.syntax.EncoderOps

class GenericErrorResponseSpec extends ImprovedFlatSpec {

  "a generic error message" should "have a default message" in {
    assert(GenericErrorResponse().message === "An error occurred")
  }

  it can "be encoded to json" in {
    assert(GenericErrorResponse("hello").asJson === Json.obj("message" -> Json.fromString("hello")))
  }

}
