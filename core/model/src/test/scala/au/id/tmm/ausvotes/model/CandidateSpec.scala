package au.id.tmm.ausvotes.model

import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, SenateElectionForState}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import io.circe.Json
import io.circe.syntax.EncoderOps

class CandidateSpec extends ImprovedFlatSpec {

  "a candidate" can "be encoded to json" in {
    val candidate = Candidate(
      election = SenateElectionForState(SenateElection.`2016`, State.VIC).right.get,
      name = Name("Jane", "Doe"),
      party = None,
      id = Candidate.Id(42),
    )

    val json = Json.obj(
      "election" -> candidate.election.asJson,
      "name" -> candidate.name.asJson,
      "party" -> candidate.party.asJson,
      "id" -> candidate.id.asJson,
    )

    assert(candidate.asJson === json)
  }

  it can "be decoded from json" in {
    val candidate = Candidate(
      election = SenateElectionForState(SenateElection.`2016`, State.VIC).right.get,
      name = Name("Jane", "Doe"),
      party = None,
      id = Candidate.Id(42),
    )

    val json = Json.obj(
      "election" -> candidate.election.asJson,
      "name" -> candidate.name.asJson,
      "party" -> candidate.party.asJson,
      "id" -> candidate.id.asJson,
    )

    assert(json.as[Candidate[SenateElectionForState]] === Right(candidate))
  }

}
