package au.id.tmm.ausvotes.model.stv

import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, SenateElectionForState}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.Json
import io.circe.syntax.EncoderOps

class StvCandidateSpec extends ImprovedFlatSpec {

  "an stv candidate" can "be encoded to json" in {
    val stvCandidate = StvCandidate(
      election = SenateElectionForState(SenateElection.`2016`, State.VIC).right.get,
      candidate = "candidate",
      position = CandidatePosition(BallotGroup.Code.unsafeMake("A"), 0),
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
      election = SenateElectionForState(SenateElection.`2016`, State.VIC).right.get,
      candidate = "candidate",
      position = CandidatePosition(BallotGroup.Code.unsafeMake("A"), 0),
    )

    val json = Json.obj(
      "election" -> stvCandidate.election.asJson,
      "candidate" -> stvCandidate.candidate.asJson,
      "position" -> stvCandidate.position.asJson,
    )

    assert(json.as[StvCandidate[SenateElectionForState, String]] === Right(stvCandidate))
  }

}
