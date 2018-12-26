package au.id.tmm.ausvotes.model.stv

import au.id.tmm.ausvotes.model.Party
import au.id.tmm.ausvotes.model.federal.SenateElection
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.Json
import io.circe.syntax.EncoderOps

class BallotGroupSpec extends ImprovedFlatSpec {

  "a grouped ballot group" can "not have a code of UG" in {
    assert(Group(SenateElection.`2016`, Ungrouped.code, party = None) === Left(Group.InvalidGroupCode))
  }

  it can "be encoded to json" in {
    val group: BallotGroup[SenateElection] = Group[SenateElection](
      SenateElection.`2016`,
      BallotGroup.Code.unsafeMake("A"),
      Some(Party("Australian Labor Party")),
    ).right.get

    val json = Json.obj(
      "election" -> group.election.asJson,
      "code" -> group.code.asJson,
      "party" -> Party("Australian Labor Party").asJson,
    )

    assert(group.asJson === json)
  }

  it can "be decoded from json" in {
    val group: BallotGroup[SenateElection] = Group[SenateElection](
      SenateElection.`2016`,
      BallotGroup.Code.unsafeMake("A"),
      Some(Party("Australian Labor Party")),
    ).right.get

    val json = Json.obj(
      "election" -> group.election.asJson,
      "code" -> group.code.asJson,
      "party" -> Party("Australian Labor Party").asJson,
    )

    assert(json.as[BallotGroup[SenateElection]] === Right(group))
  }

  "an ungrouped ballot group" should "have a code of UG" in {
    assert(Ungrouped.code.asString === "UG")
  }

  it should "have the ungrouped code" in {
    assert(Ungrouped(SenateElection.`2016`).code === Ungrouped.code)
  }

  "a ballot group code" should "have an index for a single-letter code" in {
    assert(BallotGroup.Code("D").map(_.index) === Right(3))
  }

  it should "have an index for a double-letter code" in {
    assert(BallotGroup.Code("AA").map(_.index) === Right(26))
  }

  it should "have an index for an ungrouped code" in {
    assert(BallotGroup.Code("UG").map(_.index) === Right(Int.MaxValue))
  }

  it can "not be constructed invalidly" in {
    assert(BallotGroup.Code("invalid") === Left(BallotGroup.Code.InvalidCode("invalid")))
  }

}
