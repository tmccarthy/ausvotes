package au.id.tmm.ausvotes.api.model

import argonaut.Argonaut._
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class GenericErrorResponseSpec extends ImprovedFlatSpec {

  "a generic error message" should "have a default message" in {
    assert(GenericErrorResponse().message === "An error occurred")
  }

  it can "be encoded to json" in {
    assert(GenericErrorResponse("hello").asJson === jObjectFields("message" -> jString("hello")))
  }

}
