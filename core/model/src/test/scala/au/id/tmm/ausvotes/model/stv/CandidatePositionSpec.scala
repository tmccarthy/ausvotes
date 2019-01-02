package au.id.tmm.ausvotes.model.stv

import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Json}

class CandidatePositionSpec extends ImprovedFlatSpec {

  private val testGroup = Group("election", BallotGroup.Code.unsafeMake("AA"), party = None).right.get
  private val testUngrouped = Ungrouped("election")
  private val testPosition = CandidatePosition(testGroup, 2)
  private implicit val candidatePositionDecoder: Decoder[CandidatePosition[String]] =
    CandidatePosition.decoderUsing(allGroups = List(testGroup), ungrouped = testUngrouped)

  "a candidate position" can "be encoded to json" in {
    assert(testPosition.asJson === Json.fromString("AA2"))
  }

  it can "be decoded from json" in {
    assert(Json.fromString("AA2").as[CandidatePosition[String]] === Right(testPosition))
  }

  it can "be decoded from json if it is ungrouped" in {
    assert(Json.fromString("UG3").as[CandidatePosition[String]] === Right(CandidatePosition(testUngrouped, 3)))
  }

  it should "have an ordering" in {
    val a0 = CandidatePosition(Group("election", BallotGroup.Code.unsafeMake("A"), party = None).right.get, 0)
    val a1 = CandidatePosition(Group("election", BallotGroup.Code.unsafeMake("A"), party = None).right.get, 1)
    val a2 = CandidatePosition(Group("election", BallotGroup.Code.unsafeMake("A"), party = None).right.get, 2)
    val b1 = CandidatePosition(Group("election", BallotGroup.Code.unsafeMake("B"), party = None).right.get, 1)
    val c3 = CandidatePosition(Group("election", BallotGroup.Code.unsafeMake("C"), party = None).right.get, 3)

    val positions = List(c3, a2, a1, b1, a0)
    val sorted = positions.sorted

    assert(sorted === List(a0, a1, a2, b1, c3))
  }

}
