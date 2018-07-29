package au.id.tmm.ausvotes.core.model.codecs

import argonaut.Argonaut._
import argonaut.DecodeResult
import au.id.tmm.ausvotes.core.fixtures.GroupFixture
import au.id.tmm.ausvotes.core.model.parsing.BallotGroup
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class GroupCodecSpec extends ImprovedFlatSpec {

  private implicit val partyCodec: PartyCodec = PartyCodec()
  private implicit val sut: GroupCodec = GroupCodec()

  private val groupFixture = GroupFixture.ACT

  "the group codec" should "encode a group" in {
    val group: BallotGroup = groupFixture.ALP_GROUP

    val expectedJson = jObjectFields(
      "election" -> jString("2016"),
      "state" -> jString("ACT"),
      "code" -> jString("C"),
      "party" -> groupFixture.ALP_GROUP.party.asJson,
    )

    assert(group.asJson === expectedJson)
  }

  it should "encode ungrouped" in {
    val group: BallotGroup = groupFixture.ungrouped

    val expectedJson = jObjectFields(
      "election" -> jString("2016"),
      "state" -> jString("ACT"),
      "code" -> jString("UG"),
    )

    assert(group.asJson === expectedJson)
  }

  it should "decode a group" in {
    val json = jObjectFields(
      "election" -> jString("2016"),
      "state" -> jString("ACT"),
      "code" -> jString("C"),
      "party" -> groupFixture.ALP_GROUP.party.asJson,
    )

    assert(json.as[BallotGroup] === DecodeResult.ok(groupFixture.ALP_GROUP))
  }

  it should "decode ungrouped" in {
    val json = jObjectFields(
      "election" -> jString("2016"),
      "state" -> jString("ACT"),
      "code" -> jString("UG"),
    )

    assert(json.as[BallotGroup] === DecodeResult.ok(groupFixture.ungrouped))
  }

  it should "fail to decode when there is no party" in {
    val json = jObjectFields(
      "election" -> jString("2016"),
      "state" -> jString("ACT"),
      "code" -> jString("C"),
    )

    assert(json.as[BallotGroup].isError)
  }

}
