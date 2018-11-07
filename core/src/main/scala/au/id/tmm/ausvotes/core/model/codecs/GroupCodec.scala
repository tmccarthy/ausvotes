package au.id.tmm.ausvotes.core.model.codecs

import argonaut.Argonaut._
import argonaut._
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.codecs.GeneralCodecs._
import au.id.tmm.ausvotes.core.model.flyweights.GroupFlyweight
import au.id.tmm.ausvotes.core.model.parsing.{Group, Party}
import au.id.tmm.utilities.geo.australia.State

object GroupCodec {

  implicit def encodeGroup(implicit encodeParty: EncodeJson[Party]): EncodeJson[Group] = group =>
    jObjectFields(
      "election" -> group.election.asJson,
      "state" -> group.state.asJson,
      "code" -> group.code.asJson,
      "party" -> group.party.asJson,
    )

  implicit def decodeGroup(implicit decodeParty: DecodeJson[Party]): DecodeJson[Group] = {
    val groupFlyweight: GroupFlyweight = GroupFlyweight()

    DecodeJson { cursor =>
      for {
        election <- cursor.downField("election").as[SenateElection]
        state <- cursor.downField("state").as[State]
        code <- cursor.downField("code").as[String]
        party <- cursor.downField("party").as[Party]
      } yield groupFlyweight(election, state, code, party)
    }
  }

}
