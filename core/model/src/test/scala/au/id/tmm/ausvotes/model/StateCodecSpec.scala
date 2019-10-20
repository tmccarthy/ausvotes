package au.id.tmm.ausvotes.model

import au.id.tmm.ausvotes.model.StateCodec.codec
import au.id.tmm.ausgeo.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.Json
import io.circe.syntax.EncoderOps

class StateCodecSpec extends ImprovedFlatSpec {

  "the state encoder" can "encode a state" in {
    assert((State.VIC: State).asJson === Json.fromString("VIC"))
  }

  "the state decoder" can "decode a state" in {
    assert(Json.fromString("VIC").as[State] === Right(State.VIC))
  }

}
