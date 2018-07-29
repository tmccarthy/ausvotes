package au.id.tmm.ausvotes.core.model.codecs

import argonaut.Argonaut._
import argonaut._
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.flyweights.GroupFlyweight
import au.id.tmm.ausvotes.core.model.parsing.{BallotGroup, Group, Party, Ungrouped}
import au.id.tmm.utilities.geo.australia.State

final class GroupCodec private (private val groupFlyweight: GroupFlyweight)(implicit partyCodec: PartyCodec)
  extends EncodeJson[BallotGroup] with DecodeJson[BallotGroup] {

  import GeneralCodecs._

  override def encode(ballotGroup: BallotGroup): Json = {
    val baseObject = jObjectFields(
      "election" -> ballotGroup.election.asJson,
      "state" -> ballotGroup.state.asJson,
      "code" -> ballotGroup.code.asJson,
    )

    ballotGroup match {
      case Group(_, _, _, party) => baseObject.withObject(_ :+ ("party" -> party.asJson))
      case Ungrouped(_, _) => baseObject
    }
  }

  override def decode(cursor: HCursor): DecodeResult[BallotGroup] = {
    for {
      election <- cursor.downField("election").as[SenateElection]
      state <- cursor.downField("state").as[State]
      code <- cursor.downField("code").as[String]
      party <- cursor.downField("party").as[Option[Party]]
      group <- (code, party) match {
        case (Ungrouped.code, None) => DecodeResult.ok(Ungrouped(election, state))
        case (groupCode, Some(groupParty)) => DecodeResult.ok(Group(election, state, groupCode, groupParty))
        case (_, None) => DecodeResult.fail(s"No party for group '$code'", cursor.history)
      }
    } yield group
  }

}

object GroupCodec {
  def apply(groupFlyweight: GroupFlyweight = GroupFlyweight())(implicit partyCodec: PartyCodec): GroupCodec =
    new GroupCodec(groupFlyweight)
}