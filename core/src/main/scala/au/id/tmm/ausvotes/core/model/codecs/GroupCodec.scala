package au.id.tmm.ausvotes.core.model.codecs

import argonaut.Argonaut._
import argonaut._
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.flyweights.GroupFlyweight
import au.id.tmm.ausvotes.core.model.parsing.{Group, Party}
import au.id.tmm.utilities.geo.australia.State

final class GroupCodec private (private val groupFlyweight: GroupFlyweight)(implicit partyCodec: PartyCodec)
  extends EncodeJson[Group] with DecodeJson[Group] {

  import GeneralCodecs._

  override def encode(group: Group): Json = {
    jObjectFields(
      "election" -> group.election.asJson,
      "state" -> group.state.asJson,
      "code" -> group.code.asJson,
      "party" -> group.party.asJson,
    )
  }

  override def decode(cursor: HCursor): DecodeResult[Group] = {
    for {
      election <- cursor.downField("election").as[SenateElection]
      state <- cursor.downField("state").as[State]
      code <- cursor.downField("code").as[String]
      party <- cursor.downField("party").as[Party]
    } yield Group(election, state, code, party)
  }

}

object GroupCodec {
  def apply(groupFlyweight: GroupFlyweight = GroupFlyweight())(implicit partyCodec: PartyCodec): GroupCodec =
    new GroupCodec(groupFlyweight)
}