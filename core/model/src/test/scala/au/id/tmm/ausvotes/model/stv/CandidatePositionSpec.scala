package au.id.tmm.ausvotes.model.stv

import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.Json
import io.circe.syntax.EncoderOps

class CandidatePositionSpec extends ImprovedFlatSpec {

  "a candidate position" can "be encoded to json" in {
    assert(CandidatePosition(BallotGroup.Code.unsafeMake("AA"), 2).asJson === Json.fromString("AA2"))
  }

  it can "be decoded from json" in {
    assert(Json.fromString("AA2").as[CandidatePosition] === Right(CandidatePosition(BallotGroup.Code.unsafeMake("AA"), 2)))
  }

}
