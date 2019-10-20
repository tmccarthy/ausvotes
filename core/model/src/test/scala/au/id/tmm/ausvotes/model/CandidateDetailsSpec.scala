package au.id.tmm.ausvotes.model

import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, SenateElectionForState}
import au.id.tmm.ausgeo.State
import io.circe.Json
import io.circe.syntax.EncoderOps
import org.scalatest.FlatSpec

class CandidateDetailsSpec extends FlatSpec {

  "a candidate" can "be encoded to json" in {
    val candidate = CandidateDetails(
      election = SenateElection.`2016`.electionForState(State.VIC).get,
      name = Name("Jane", "Doe"),
      party = None,
      id = CandidateDetails.Id(42),
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
    val candidate = CandidateDetails(
      election = SenateElection.`2016`.electionForState(State.VIC).get,
      name = Name("Jane", "Doe"),
      party = None,
      id = CandidateDetails.Id(42),
    )

    val json = Json.obj(
      "election" -> candidate.election.asJson,
      "name" -> candidate.name.asJson,
      "party" -> candidate.party.asJson,
      "id" -> candidate.id.asJson,
    )

    assert(json.as[CandidateDetails[SenateElectionForState]] === Right(candidate))
  }

}
