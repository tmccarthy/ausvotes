package au.id.tmm.ausvotes.core.model.codecs

import argonaut.Argonaut._
import argonaut._
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing._
import au.id.tmm.utilities.geo.australia.State

object CandidateCodec {
  private val candidatePositionCodePattern = "([A-Z]+)(\\d+)".r

  def apply(groups: Set[Group])(implicit partyCodec: PartyCodec): CodecJson[Candidate] = {
    val encoder = EncodeCandidate()
    val decoder = DecodeCandidate(groups)

    CodecJson(encoder.encode, decoder.decode)
  }

  final class EncodeCandidate private (
                                        implicit private val partyCodec: PartyCodec,
                                      ) extends EncodeJson[Candidate] {
    import GeneralCodecs._

    override def encode(candidate: Candidate): Json = jObjectFields(
      "election" -> candidate.election.asJson,
      "state" -> candidate.state.asJson,
      "aecId" -> candidate.aecId.asJson,
      "name" -> candidate.name.asJson,
      "party" -> candidate.party.asJson,
      "btlPosition" -> candidate.btlPosition.code.asJson
    )
  }

  object EncodeCandidate {
    def apply()(implicit partyCodec: PartyCodec): EncodeCandidate = new EncodeCandidate()
  }

  final class DecodeCandidate private (
                                        private val groups: Set[Group],
                                      )(
                                        implicit private val partyCodec: PartyCodec,
                                      ) extends DecodeJson[Candidate] {
    import GeneralCodecs._

    private val groupLookup: Map[(SenateElection, State, String), Group] = groups.groupBy { group =>
      (group.election, group.state, group.code)
    }.mapValues(_.head)

    private def lookupBallotGroup(election: SenateElection, state: State, groupCode: String): Option[BallotGroup] =
      if (groupCode == Ungrouped.code) {
        Some(Ungrouped(election, state))
      } else {
        groupLookup.get((election, state, groupCode))
      }

    override def decode(cursor: HCursor): DecodeResult[Candidate] = for {
      election <- cursor.downField("election").as[SenateElection]
      state <- cursor.downField("state").as[State]
      aecId <- cursor.downField("aecId").as[String]
      name <- cursor.downField("name").as[Name]
      party <- cursor.downField("party").as[Party]
      btlPosition <- decodeBtlPositionCode(cursor)
      (groupCode, btlIndex) = btlPosition
      group <- lookupBallotGroup(election, state, groupCode)
        .map(DecodeResult.ok)
        .getOrElse(DecodeResult.fail(s"Unrecognised group ${groupCode}", cursor.history))
    } yield Candidate(
      election,
      state,
      aecId,
      name,
      party,
      CandidatePosition(
        group,
        btlIndex,
      )
    )

    private def decodeBtlPositionCode(cursor: HCursor): DecodeResult[(String, Int)] =
      cursor.downField("btlPosition").as[String].flatMap {
        case candidatePositionCodePattern(groupCode, position) => DecodeResult.ok((groupCode, position.toInt))
        case badCode => DecodeResult.fail(s"Could not parse btl position from '$badCode'", cursor.history)
      }
  }

  object DecodeCandidate {
    def apply(groups: Set[Group])(implicit partyCodec: PartyCodec): DecodeJson[Candidate] = new DecodeCandidate(groups)
  }

}
