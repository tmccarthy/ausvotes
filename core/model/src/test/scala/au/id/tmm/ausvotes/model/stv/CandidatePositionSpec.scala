package au.id.tmm.ausvotes.model.stv

import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Json}

class CandidatePositionSpec extends ImprovedFlatSpec {

  private val testGroup = Group("election", BallotGroup.Code.unsafeMake("AA"), party = None).right.get
  private val testPosition = CandidatePosition(testGroup, 2)
  private implicit val candidatePositionDecoder: Decoder[CandidatePosition[String]] = CandidatePosition.decoderUsing(allGroups = List(testGroup))

  "a candidate position" can "be encoded to json" in {
    assert(testPosition.asJson === Json.fromString("AA2"))
  }

  it can "be decoded from json" in {
    assert(Json.fromString("AA2").as[CandidatePosition[String]] === Right(testPosition))
  }

}
