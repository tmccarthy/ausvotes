package au.id.tmm.ausvotes.core.model.codecs

import argonaut.Argonaut._
import argonaut._
import au.id.tmm.ausvotes.core.fixtures.CandidateFixture.ACT._
import au.id.tmm.ausvotes.core.fixtures.GroupFixture
import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.core.model.parsing.{Candidate, Name}
import au.id.tmm.countstv.model.values.{Count, NumPapers, NumVotes, Ordinal}
import au.id.tmm.countstv.model.{CandidateStatus, CandidateStatuses, VoteCount}
import au.id.tmm.utilities.geo.australia.State
import au.id.tmm.utilities.testing.ImprovedFlatSpec

class GeneralCodecsSpec extends ImprovedFlatSpec {

  import CandidateCodec.encodeCandidate
  import GeneralCodecs._
  import PartyCodec._

  private implicit val candidateDecoder: DecodeJson[Candidate] = CandidateCodec.decodeCandidate(GroupFixture.ACT.groups)

  "the election codec" should "encode an election" in {
    val election: SenateElection = SenateElection.`2016`

    assert(election.asJson === jString("2016"))
  }

  it should "decode an election" in {
    val json = jString("2016")

    assert(json.as[SenateElection] === DecodeResult.ok(SenateElection.`2016`))
  }

  it should "fail to decode if the election is not recognised" in {
    val json = jString("invalid")

    assert(json.as[SenateElection].isError)
  }

  "the state codec" should "encode a state" in {
    val state: State = State.SA

    assert(state.asJson === jString("SA"))
  }

  it should "decode a state" in {
    val json = jString("SA")

    assert(json.as[State] === DecodeResult.ok(State.SA))
  }

  it should "fail to decode if the election is not recognised" in {
    val json = jString("invalid")

    assert(json.as[State].isError)
  }

  "the name codec" should "encode a name" in {
    val name = Name("Jane", "Doe")

    val expectedJson = jObjectFields(
      "givenNames" -> jString("Jane"),
      "surname" -> jString("Doe"),
    )

    assert(name.asJson === expectedJson)
  }

  it should "decode a name" in {
    val json = jObjectFields(
      "givenNames" -> jString("Jane"),
      "surname" -> jString("Doe"),
    )

    val expectedName = Name("Jane", "Doe")

    assert(json.as[Name] === DecodeResult.ok(expectedName))
  }

  "the count codec" should "encode a count" in {
    assert(Count(1).asJson === jNumber(1))
  }

  it should "decode a count" in {
    assert(jNumber(1).as[Count] === DecodeResult.ok(Count(1)))
  }

  "the ordinal codec" should "encode an ordinal" in {
    assert(Ordinal(1).asJson === jNumber(1))
  }

  it should "decode an ordinal" in {
    assert(jNumber(1).as[Ordinal] === DecodeResult.ok(Ordinal(1)))
  }

  "the vote count codec" should "encode a vote count" in {
    val expectedJson = jObjectFields(
      "papers" -> jNumber(1),
      "votes" -> jNumber(2),
    )

    assert(VoteCount(NumPapers(1), NumVotes(2)).asJson === expectedJson)
  }

  it should "decode a vote count" in {
    val json = jObjectFields(
      "papers" -> jNumber(1),
      "votes" -> jNumber(2),
    )

    assert(json.as[VoteCount] === DecodeResult.ok(VoteCount(NumPapers(1), NumVotes(2))))
  }

  "the candidate statuses codec" can "encode candidate statuses" in {
    val candidateStatuses = CandidateStatuses[Candidate](
      katyGallagher -> CandidateStatus.Elected(Ordinal.first, Count(1)),
      zedSeselja -> CandidateStatus.Elected(Ordinal.second, Count(1)),
      anthonyHanson -> CandidateStatus.Excluded(Ordinal.first, Count(1)),
      christinaHobbs -> CandidateStatus.Remaining,
      mattDonnelly -> CandidateStatus.Ineligible,
    )

    val expectedJson = jArrayElements(
      jObjectFields(
        "candidate" -> katyGallagher.asJson,
        "outcome" -> jObjectFields(
          "status" -> jString("elected"),
          "ordinal" -> jNumber(0),
          "count" -> jNumber(1),
        )
      ),
      jObjectFields(
        "candidate" -> zedSeselja.asJson,
        "outcome" -> jObjectFields(
          "status" -> jString("elected"),
          "ordinal" -> jNumber(1),
          "count" -> jNumber(1),
        )
      ),
      jObjectFields(
        "candidate" -> christinaHobbs.asJson,
        "outcome" -> jObjectFields(
          "status" -> jString("remaining"),
        ),
      ),
      jObjectFields(
        "candidate" -> anthonyHanson.asJson,
        "outcome" -> jObjectFields(
          "status" -> jString("excluded"),
          "ordinal" -> jNumber(0),
          "count" -> jNumber(1),
        )
      ),
      jObjectFields(
        "candidate" -> mattDonnelly.asJson,
        "outcome" -> jObjectFields(
          "status" -> jString("ineligible"),
        ),
      ),
    )

    assert(candidateStatuses.asJson === expectedJson)
  }

  it can "decode candidate statuses" in {
    val json = jArrayElements(
      jObjectFields(
        "candidate" -> katyGallagher.asJson,
        "outcome" -> jObjectFields(
          "status" -> jString("elected"),
          "ordinal" -> jNumber(0),
          "count" -> jNumber(1),
        )
      ),
      jObjectFields(
        "candidate" -> zedSeselja.asJson,
        "outcome" -> jObjectFields(
          "status" -> jString("elected"),
          "ordinal" -> jNumber(1),
          "count" -> jNumber(1),
        )
      ),
      jObjectFields(
        "candidate" -> christinaHobbs.asJson,
        "outcome" -> jObjectFields(
          "status" -> jString("remaining"),
        ),
      ),
      jObjectFields(
        "candidate" -> anthonyHanson.asJson,
        "outcome" -> jObjectFields(
          "status" -> jString("excluded"),
          "ordinal" -> jNumber(0),
          "count" -> jNumber(1),
        )
      ),
      jObjectFields(
        "candidate" -> mattDonnelly.asJson,
        "outcome" -> jObjectFields(
          "status" -> jString("ineligible"),
        ),
      ),
    )

    val expectedCandidateStatuses = CandidateStatuses[Candidate](
      katyGallagher -> CandidateStatus.Elected(Ordinal.first, Count(1)),
      zedSeselja.copy(party = zedSeselja.party.nationalEquivalent) -> CandidateStatus.Elected(Ordinal.second, Count(1)),
      anthonyHanson -> CandidateStatus.Excluded(Ordinal.first, Count(1)),
      christinaHobbs -> CandidateStatus.Remaining,
      mattDonnelly.copy(party = mattDonnelly.party.nationalEquivalent) -> CandidateStatus.Ineligible,
    )

    assert(json.as[CandidateStatuses[Candidate]] === DecodeResult.ok(expectedCandidateStatuses))
  }

  "the candidate status codec" should "fail to decode an invalid candidate status" in {
    val json = jObjectFields(
      "status" -> jString("invalid"),
    )

    assert(json.as[CandidateStatus].message === Some("Invalid candidate status \"invalid\""))
  }

}
