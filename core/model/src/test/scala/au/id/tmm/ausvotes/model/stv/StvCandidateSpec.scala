package au.id.tmm.ausvotes.model.stv

import au.id.tmm.ausvotes.model.federal.senate.{SenateElection, SenateElectionForState}
import au.id.tmm.ausvotes.model.{CandidateDetails, Name}
import au.id.tmm.ausgeo.State
import au.id.tmm.utilities.testing.syntax._
import org.scalatest.FlatSpec
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Json}

class StvCandidateSpec extends FlatSpec {

  private val election = SenateElection.`2016`.electionForState(State.VIC).get
  private val group = Group(election, BallotGroup.Code.unsafeMake("A"), party = None).get
  private implicit val candidatePositionDecoder: Decoder[CandidatePosition[SenateElectionForState]] =
    CandidatePosition.decoderUsing(allGroups = List(group), ungrouped = Ungrouped(election))

  "an stv candidate" can "be encoded to json" in {
    val stvCandidate = StvCandidate(
      election = election,
      candidateDetails = CandidateDetails[SenateElectionForState](
        election,
        Name("Jane", "Doe"),
        party = None,
        id = CandidateDetails.Id(1),
      ),
      position = CandidatePosition(group, 0),
    )

    val json = Json.obj(
      "election" -> stvCandidate.election.asJson,
      "candidateDetails" -> stvCandidate.candidateDetails.asJson,
      "position" -> stvCandidate.position.asJson,
    )

    assert(stvCandidate.asJson === json)
  }

  it can "be decoded from json" in {
    val stvCandidate = StvCandidate(
      election = election,
      candidateDetails = CandidateDetails[SenateElectionForState](
        election,
        Name("Jane", "Doe"),
        party = None,
        id = CandidateDetails.Id(1),
      ),
      position = CandidatePosition(group, 0),
    )

    val json = Json.obj(
      "election" -> stvCandidate.election.asJson,
      "candidateDetails" -> stvCandidate.candidateDetails.asJson,
      "position" -> stvCandidate.position.asJson,
    )

    assert(json.as[StvCandidate[SenateElectionForState]] === Right(stvCandidate))
  }

}
