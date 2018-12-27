package au.id.tmm.ausvotes.model.stv

import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, SenateElectionForState}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Json}

class StvCandidateSpec extends ImprovedFlatSpec {

  private val election = SenateElectionForState(SenateElection.`2016`, State.VIC).right.get
  private val group = Group(election, BallotGroup.Code.unsafeMake("A"), party = None).right.get
  private implicit val candidatePositionDecoder: Decoder[CandidatePosition[SenateElectionForState]] =
    CandidatePosition.decoderUsing(allGroups = List(group))

  "an stv candidate" can "be encoded to json" in {
    val stvCandidate = StvCandidate(
      election = election,
      candidate = "candidate",
      position = CandidatePosition(group, 0),
    )

    val json = Json.obj(
      "election" -> stvCandidate.election.asJson,
      "candidate" -> stvCandidate.candidate.asJson,
      "position" -> stvCandidate.position.asJson,
    )

    assert(stvCandidate.asJson === json)
  }

  it can "be decoded from json" in {
    val stvCandidate = StvCandidate(
      election = election,
      candidate = "candidate",
      position = CandidatePosition(group, 0),
    )

    val json = Json.obj(
      "election" -> stvCandidate.election.asJson,
      "candidate" -> stvCandidate.candidate.asJson,
      "position" -> stvCandidate.position.asJson,
    )

    assert(json.as[StvCandidate[SenateElectionForState, String]] === Right(stvCandidate))
  }

}
