package au.id.tmm.ausvotes.core.model.codecs

import argonaut.Argonaut._
import argonaut._
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing._
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class CandidateCodecSpec extends ImprovedFlatSpec {

  private val testGroup = Group(
    SenateElection.`2016`,
    State.SA,
    "AA",
    Party.RegisteredParty.ALP,
  )

  import CandidateCodec._
  import PartyCodec._

  private implicit val candidateDecoder: DecodeJson[Candidate] = CandidateCodec.decodeCandidate(Set(testGroup))

  "the candidate codec" can "encode a grouped candidate" in {
    val candidate = Candidate(
      election = SenateElection.`2016`,
      state = State.SA,
      aecId = "1234",
      name = Name("Jane", "Doe"),
      party = Party.RegisteredParty.ALP,
      btlPosition = CandidatePosition(
        testGroup,
        0,
      )
    )

    val expectedJson = jObjectFields(
      "election" -> jString("2016"),
      "state" -> jString("SA"),
      "aecId" -> jString("1234"),
      "name" -> jObjectFields(
        "givenNames" -> jString("Jane"),
        "surname" -> jString("Doe"),
      ),
      "party" -> jString("Australian Labor Party"),
      "btlPosition" -> jString("AA0"),
    )

    assert(candidate.asJson === expectedJson)
  }

  it can "encode an ungrouped candidate" in {
    val candidate = Candidate(
      election = SenateElection.`2016`,
      state = State.SA,
      aecId = "1234",
      name = Name("Jane", "Doe"),
      party = Party.Independent,
      btlPosition = CandidatePosition(
        Ungrouped(SenateElection.`2016`, State.SA),
        3,
      )
    )

    val expectedJson = jObjectFields(
      "election" -> jString("2016"),
      "state" -> jString("SA"),
      "aecId" -> jString("1234"),
      "name" -> jObjectFields(
        "givenNames" -> jString("Jane"),
        "surname" -> jString("Doe"),
      ),
      "party" -> jString("independent"),
      "btlPosition" -> jString("UG3"),
    )

    assert(candidate.asJson === expectedJson)
  }

  it can "decode a grouped candidate" in {
    val json = jObjectFields(
      "election" -> jString("2016"),
      "state" -> jString("SA"),
      "aecId" -> jString("1234"),
      "name" -> jObjectFields(
        "givenNames" -> jString("Jane"),
        "surname" -> jString("Doe"),
      ),
      "party" -> jString("Australian Labor Party"),
      "btlPosition" -> jString("AA0"),
    )

    val expectedCandidate = Candidate(
      election = SenateElection.`2016`,
      state = State.SA,
      aecId = "1234",
      name = Name("Jane", "Doe"),
      party = Party.RegisteredParty.ALP,
      btlPosition = CandidatePosition(
        testGroup,
        0,
      )
    )

    assert(json.as[Candidate] === DecodeResult.ok(expectedCandidate))
  }

  it can "decode an ungrouped candidate" in {
    val json = jObjectFields(
      "election" -> jString("2016"),
      "state" -> jString("SA"),
      "aecId" -> jString("1234"),
      "name" -> jObjectFields(
        "givenNames" -> jString("Jane"),
        "surname" -> jString("Doe"),
      ),
      "party" -> jString("independent"),
      "btlPosition" -> jString("UG3"),
    )

    val expectedCandidate = Candidate(
      election = SenateElection.`2016`,
      state = State.SA,
      aecId = "1234",
      name = Name("Jane", "Doe"),
      party = Party.Independent,
      btlPosition = CandidatePosition(
        Ungrouped(SenateElection.`2016`, State.SA),
        3,
      )
    )

    assert(json.as[Candidate] === DecodeResult.ok(expectedCandidate))
  }

  it should "fail to decode when the btl position is malformed" in {
    val json = jObjectFields(
      "election" -> jString("2016"),
      "state" -> jString("SA"),
      "aecId" -> jString("1234"),
      "name" -> jObjectFields(
        "givenNames" -> jString("Jane"),
        "surname" -> jString("Doe"),
      ),
      "party" -> jString("Australian Labor Party"),
      "btlPosition" -> jString("invalid"),
    )

    assert(json.as[Candidate].isError)
  }

  it should "fail to decode when the btl group is unrecognised" in {
    val json = jObjectFields(
      "election" -> jString("2016"),
      "state" -> jString("SA"),
      "aecId" -> jString("1234"),
      "name" -> jObjectFields(
        "givenNames" -> jString("Jane"),
        "surname" -> jString("Doe"),
      ),
      "party" -> jString("Australian Labor Party"),
      "btlPosition" -> jString("AB0"),
    )

    assert(json.as[Candidate].isError)
  }

}
