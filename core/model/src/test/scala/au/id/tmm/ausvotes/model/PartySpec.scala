package au.id.tmm.ausvotes.model

import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.Json
import io.circe.syntax.EncoderOps

class PartySpec extends ImprovedFlatSpec {

  "a party" can "be encoded to json" in {
    assert(Party("Australian Labor Party").asJson === Json.fromString("Australian Labor Party"))
  }

  it can "be decoded from json" in {
    assert(Json.fromString("Australian Labor Party").as[Party] === Right(Party("Australian Labor Party")))
  }

}
